if [ ! -d "android-studio" ]; then
  wget https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2024.2.1.11/android-studio-2024.2.1.11-linux.tar.gz
  tar -xvzf android-studio-2024.2.1.11-linux.tar.gz
fi

cd android-studio/bin
./studio.sh