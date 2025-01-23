#include <linux/module.h>
#include <linux/usb.h>
#include <linux/slab.h>
#include <linux/fs.h>

MODULE_AUTHOR("DevTITANS <devtitans@icomp.ufam.edu.br>");
MODULE_DESCRIPTION("Driver de acesso ao SmartInfrared (ESP32 com Chip Serial CP2102");
MODULE_LICENSE("GPL");

#define MAX_RECV_LINE 100

static struct usb_device *smartinfrared_device;
static uint usb_in, usb_out;
static char *usb_in_buffer, *usb_out_buffer;
static int usb_max_size;

#define VENDOR_ID   0x10c4
#define PRODUCT_ID  0xea60
static const struct usb_device_id id_table[] = { { USB_DEVICE(VENDOR_ID, PRODUCT_ID) }, {} };

static int usb_probe(struct usb_interface *ifce, const struct usb_device_id *id);
static void usb_disconnect(struct usb_interface *ifce);
static int usb_write_serial(const char *cmd);
static int usb_read_serial(char *response, size_t size);

static ssize_t attr_show(struct kobject *sys_obj, struct kobj_attribute *attr, char *buff);
static ssize_t attr_store(struct kobject *sys_obj, struct kobj_attribute *attr, const char *buff, size_t count);

static struct kobj_attribute pattern_attribute = __ATTR(pattern, 0660, attr_show, attr_store);
static struct attribute *attrs[] = { &pattern_attribute.attr, NULL };
static struct attribute_group attr_group = { .attrs = attrs };
static struct kobject *sys_obj;

MODULE_DEVICE_TABLE(usb, id_table);

static struct usb_driver smartinfrared_driver = {
    .name        = "smartinfrared",
    .probe       = usb_probe,
    .disconnect  = usb_disconnect,
    .id_table    = id_table,
};

module_usb_driver(smartinfrared_driver);

static int usb_probe(struct usb_interface *interface, const struct usb_device_id *id) {
    struct usb_endpoint_descriptor *usb_endpoint_in, *usb_endpoint_out;

    printk(KERN_INFO "SmartInfrared: Dispositivo conectado ...\n");

    sys_obj = kobject_create_and_add("smartInfrared", kernel_kobj);
    if (!sys_obj || sysfs_create_group(sys_obj, &attr_group)) {
        kobject_put(sys_obj);
        printk(KERN_ERR "SmartInfrared: Falha ao criar arquivos no SysFs\n");
        return -ENOMEM;
    }

    smartinfrared_device = interface_to_usbdev(interface);
    if (usb_find_common_endpoints(interface->cur_altsetting, &usb_endpoint_in, &usb_endpoint_out, NULL, NULL)) {
        printk(KERN_ERR "SmartInfrared: Falha ao localizar endpoints USB\n");
        return -ENODEV;
    }

    usb_max_size = usb_endpoint_maxp(usb_endpoint_in);
    usb_in = usb_endpoint_in->bEndpointAddress;
    usb_out = usb_endpoint_out->bEndpointAddress;
    usb_in_buffer = kmalloc(usb_max_size, GFP_KERNEL);
    usb_out_buffer = kmalloc(usb_max_size, GFP_KERNEL);

    return 0;
}

static void usb_disconnect(struct usb_interface *interface) {
    printk(KERN_INFO "SmartInfrared: Dispositivo desconectado.\n");
    if (sys_obj)
        kobject_put(sys_obj);
    kfree(usb_in_buffer);
    kfree(usb_out_buffer);
}

static int usb_write_serial(const char *cmd) {
    int ret, actual_size;
    printk(KERN_INFO "SmartInfrared: Enviando comando: %s\n", cmd);

    snprintf(usb_out_buffer, usb_max_size, "%s", cmd);
    ret = usb_bulk_msg(smartinfrared_device, usb_sndbulkpipe(smartinfrared_device, usb_out),
                       usb_out_buffer, strlen(usb_out_buffer), &actual_size, 5000);
    if (ret) {
        printk(KERN_ERR "SmartInfrared: Falha ao enviar comando. Código: %d\n", ret);
    }
    return ret;
}

static int usb_read_serial(char *response, size_t size) {
    
    int ret, actual_size, attempts = 0;
    char full_response[MAX_RECV_LINE] = {0};
    char *newline_ptr, *start_ptr;

    while (attempts < 50) {
        ret = usb_bulk_msg(smartinfrared_device, usb_rcvbulkpipe(smartinfrared_device, usb_in),
                           usb_in_buffer, usb_max_size, &actual_size, 5000);
        if (ret) {
            printk(KERN_INFO "SmartInfrared: Dados que deram errado: %s\n", usb_in_buffer);

            printk(KERN_ERR "SmartInfrared: Falha ao ler resposta. Código: %d\n", ret);
            return -1;
        }

        usb_in_buffer[actual_size] = '\0';
        strncat(full_response, usb_in_buffer, sizeof(full_response) - strlen(full_response) - 1);

        newline_ptr = strchr(full_response, '\n');
       if (newline_ptr) {
            *newline_ptr = '\0';
            if (strncmp(full_response, "RECV_", 5) == 0 || strncmp(full_response, "SEND_", 5) == 0) {
                snprintf(response, size, "%s", full_response);
                return 0;
            } else {
                printk(KERN_INFO "SmartInfrared: Resposta parcial ou inválida: %s\n", full_response);
                memset(full_response, 0, sizeof(full_response));
                continue;
            }
}


        attempts++;
    }

    printk(KERN_ERR "SmartInfrared: Falha ao ler resposta após várias tentativas.\n");
    return -1;
}

static ssize_t attr_show(struct kobject *sys_obj, struct kobj_attribute *attr, char *buff) {
    char response[MAX_RECV_LINE];
    if (usb_write_serial("RECV") < 0 || usb_read_serial(response, sizeof(response)) < 0) {
        return -EIO;
    }
    return scnprintf(buff, PAGE_SIZE, "%s\n", response);
}

static ssize_t attr_store(struct kobject *sys_obj, struct kobj_attribute *attr, const char *buff, size_t count) {
    char cmd[MAX_RECV_LINE];
    char response[MAX_RECV_LINE];

    if (strncmp(buff, "freq", 4) == 0) {
        if (strlen(buff) == 4) {
            snprintf(cmd, sizeof(cmd), "SEND_freq");
        } else {
            snprintf(cmd, sizeof(cmd), "SEND_%s", buff);
        }
    } else {
        snprintf(cmd, sizeof(cmd), "SEND_%s", buff);
    }

    if (usb_write_serial(cmd) < 0) {
        return -EIO;
    }

    if (usb_read_serial(response, sizeof(response)) < 0) {
        return -EIO;
    }

    printk(KERN_INFO "SmartInfrared: Resposta do dispositivo: %s\n", response);

    return count;
}