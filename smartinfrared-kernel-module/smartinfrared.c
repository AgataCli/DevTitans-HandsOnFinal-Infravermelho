#include <linux/module.h>
#include <linux/usb.h>
#include <linux/slab.h>

MODULE_AUTHOR("DevTITANS <devtitans@icomp.ufam.edu.br>");
MODULE_DESCRIPTION("Driver de acesso ao smartinfrared (ESP32 com Chip Serial CP2102");
MODULE_LICENSE("GPL");


#define MAX_RECV_LINE 100
int sig_value = 0;

static struct usb_device *smartinfrared_device;       
static uint usb_in, usb_out;                      
static char *usb_in_buffer, *usb_out_buffer;      
static int usb_max_size;                          

#define VENDOR_ID   0x10c4  /* Encontre o VendorID  do smartinfrared */
#define PRODUCT_ID  0xea60  /* Encontre o ProductID do smartinfrared */
static const struct usb_device_id id_table[] = { { USB_DEVICE(VENDOR_ID, PRODUCT_ID) }, {} };

static int  usb_probe(struct usb_interface *ifce, const struct usb_device_id *id);
static void usb_disconnect(struct usb_interface *ifce);                          
static int  usb_read_serial(void);
static int usb_write_serial(const char *cmd, int param);   
int usb_send_cmd(const char* command, int param);

static ssize_t attr_show(struct kobject *sys_obj, struct kobj_attribute *attr, char *buff);
static ssize_t attr_store(struct kobject *sys_obj, struct kobj_attribute *attr, const char *buff, size_t count);   
static struct kobj_attribute  sig_attribute = __ATTR(sig, S_IRUGO | S_IWUSR, attr_show, attr_store);
static struct attribute      *attrs[]       = { &sig_attribute.attr, NULL };
static struct attribute_group attr_group    = { .attrs = attrs };
static struct kobject        *sys_obj;                                            

MODULE_DEVICE_TABLE(usb, id_table);

bool ignore = true;

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


    sys_obj = kobject_create_and_add("smartinfrared", kernel_kobj);
    ignore = sysfs_create_group(sys_obj, &attr_group);


    smartinfrared_device = interface_to_usbdev(interface);
    ignore =  usb_find_common_endpoints(interface->cur_altsetting, &usb_endpoint_in, &usb_endpoint_out, NULL, NULL); 
    usb_max_size = usb_endpoint_maxp(usb_endpoint_in);
    usb_in = usb_endpoint_in->bEndpointAddress;
    usb_out = usb_endpoint_out->bEndpointAddress;
    usb_in_buffer = kmalloc(usb_max_size, GFP_KERNEL);
    usb_out_buffer = kmalloc(usb_max_size, GFP_KERNEL);

    sig_value = usb_send_cmd("SET_SIG", 0);
    printk("INITIAL SIGNAL VALUE: %d\n", sig_value);    

    return 0;
}

static void usb_disconnect(struct usb_interface *interface) {
    printk(KERN_INFO "SmartInfrared: Dispositivo desconectado.\n");
    if (sys_obj) kobject_put(sys_obj);     
    kfree(usb_in_buffer);                  
    kfree(usb_out_buffer);
}

int usb_send_cmd(const char* command, int param) {

    int response_value;
    


    response_value = usb_write_serial(command, param);


    if (response_value == -1) {
        printk(KERN_ERR "SmartInfrared: Erro ao obter a resposta da USB.\n");
    }


    return response_value;
}




static ssize_t attr_show(struct kobject *sys_obj, struct kobj_attribute *attr, char *buff) {
    int value;
    const char *attr_name = attr->attr.name;

    printk(KERN_INFO "SmartInfrared: Lendo %s ...\n", attr_name);


    if (strcmp(attr_name, "led") == 0) {
        value = usb_send_cmd("GET_SIG", -1); 
    } else {
        return -EINVAL; 
    }

    if (value < 0) {
        printk(KERN_ERR "SmartInfrared: Erro ao ler valor de %s\n", attr_name);
        return value; 
    }

    sprintf(buff, "%d\n", value); 
    return strlen(buff);
}



static ssize_t attr_store(struct kobject *sys_obj, struct kobj_attribute *attr, const char *buff, size_t count) {
    long ret, value;
    const char *attr_name = attr->attr.name;


    ret = kstrtol(buff, 10, &value);
    if (ret) {
        printk(KERN_ALERT "SmartInfrared: valor de %s invalido.\n", attr_name);
        return -EACCES;
    }
 
    printk(KERN_INFO "SmartInfrared: Setando %s para %ld ...\n", attr_name, value);


    if (strcmp(attr_name, "sig") == 0) {
        ret = usb_send_cmd("SET_SIG", value); 
    } 

   else {
        printk(KERN_ALERT "SmartInfrared: Comando desconhecido para %s.\n", attr_name);
        return -EINVAL; 
    }


    if (ret < 0) {
        printk(KERN_ALERT "SmartInfrared: Erro ao setar o valor de %s.\n", attr_name);
        return -EACCES;
    }

    return strlen(buff);
}

static int usb_write_serial(const char *cmd, int param) {
    int ret, actual_size;
    char resp_expected[MAX_RECV_LINE];   
    int response_value;
    int attempt = 0;




    if(param == -1){
        snprintf(usb_out_buffer, MAX_RECV_LINE, "%s", cmd);
    }else{
        snprintf(usb_out_buffer, MAX_RECV_LINE, "%s %d", cmd, param);
    }

    printk(KERN_INFO "SmartInfrared: Enviando comando: %s\n", usb_out_buffer);


    do {
        printk(KERN_INFO "SmartInfrared: Tentando enviar comando... {%s} %d\n", usb_out_buffer, usb_sndbulkpipe(smartinfrared_device, usb_out));

        ret = usb_bulk_msg(smartinfrared_device, usb_sndbulkpipe(smartinfrared_device, usb_out),
        usb_out_buffer, strlen(usb_out_buffer), &actual_size, 1000);
        attempt++;
        if (ret) {
        printk(KERN_ERR "SmartInfrared: -Error sending bulk message: %d\n", ret);
    
        if (ret == -ETIMEDOUT) {
            printk(KERN_ERR "SmartInfrared: Timeout occurred\n");
        
        } else if (ret == -EPIPE) {
            printk(KERN_ERR "SmartInfrared: Stalled endpoint\n");
        
        }
}

        if (ret == -EAGAIN && attempt < 10) {
            printk(KERN_INFO "SmartInfrared: Tentando novamente (tentativa %d)...\n", attempt);
            msleep(500);
        } else if (ret) {
            printk(KERN_ERR "SmartInfrared: Erro ao enviar comando! Código de erro: %d\n", ret);
            return -1;
        }

    } while (ret == -EAGAIN && attempt < 10);


    snprintf(resp_expected, MAX_RECV_LINE, "RES %s", cmd);

    response_value = usb_read_serial();
   
    if (strncmp(resp_expected, "RES GET_SIG", strlen("RES GET_SIG")) == 0) {
        printk(KERN_INFO "SmartInfrared: Valor do LED recebido: %d\n", response_value);
    }
    else if (strncmp(resp_expected, "RES SET_SIG", strlen("RES SET_SIG")) == 0) {
        if (response_value == 1) {
            printk(KERN_INFO "SmartInfrared: LED ajustado com sucesso.\n");
        } else {
            printk(KERN_ERR "SmartInfrared: Falha ao ajustar o LED.\n");
        }
    }
    else {
        printk(KERN_ERR "SmartInfrared: Comando não reconhecido ou erro na resposta.\n");
        return -1;
    }

    return response_value; 
}


static int usb_read_serial(void) {
    int ret, actual_size, attempts = 0;
    char response[MAX_RECV_LINE]; 
    char *start_ptr;
    
    memset(response, 0, sizeof(response));

    while (attempts < 20) {
    

    
        ret = usb_bulk_msg(smartinfrared_device, usb_rcvbulkpipe(smartinfrared_device, usb_in),
                           usb_in_buffer, usb_max_size, &actual_size, 1000);
        if (ret) {
            printk(KERN_ERR "SmartInfrared: Erro ao ler da porta serial! Código: %d\n", ret);
            return -1;
        }

    
        strncat(response, usb_in_buffer, actual_size);
        printk(KERN_INFO "SmartInfrared: RESPOSTA: %s\n", response);


    
        start_ptr = strchr(response, '\n');
        if (start_ptr) {
        
            *start_ptr = '\0';

        
            start_ptr = strstr(response, "RES ");
            if (start_ptr == NULL) {
                printk(KERN_INFO "SmartInfrared: Dados não reconhecidos, tentando novamente...\n");
                attempts++;
                continue;
            }

        

            if (strncmp(start_ptr, "RES GET_SIG", strlen("RES GET_SIG")) == 0) {
            
                sig_value = simple_strtol(start_ptr + strlen("RES GET_SIG "), NULL, 10);
                printk(KERN_INFO "SmartInfrared: LED Value recebido: %d\n", sig_value);
                return sig_value;
            } 
            else if (strncmp(start_ptr, "RES SET_SIG", strlen("RES SET_SIG")) == 0) {
            
                int SET_SIG_status = simple_strtol(start_ptr + strlen("RES SET_SIG "), NULL, 10);
                if (SET_SIG_status == 1) {
                    printk(KERN_INFO "SmartInfrared: LED atualizado com sucesso.\n");
                } else {
                    printk(KERN_ERR "SmartInfrared: Erro ao atualizar LED.\n");
                }
                return SET_SIG_status;
            } 
            else {
                printk(KERN_ERR "SmartInfrared: Resposta não reconhecida: %s\n", start_ptr);
                attempts++;
            }
        } else {
            printk(KERN_INFO "SmartInfrared: Aguardando por resposta completa...\n");
            attempts++;
        }
    }

    printk(KERN_ERR "SmartInfrared: Falha ao ler resposta após várias tentativas.\n");
    return -1;
}