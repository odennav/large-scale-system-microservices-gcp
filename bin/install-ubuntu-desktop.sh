sudo apt update

export DEBIAN_FRONTEND=noninteractive

sudo apt install -y xfce4

sudo apt install -y tightvncserver
sudo apt install -y xfce4-terminal
sudo apt install -y autocutsel
sudo apt install -y firefox 

printf "password\npassword\n\n" | vncpasswd
vncserver
vncserver -kill :1
echo "xfce4-session &" >> ~/.vnc/xstartup
echo "xfce4-panel &" >> ~/.vnc/xstartup
echo "x-window-manager &" >> ~/.vnc/xstartup
echo "xfce4-terminal &" >> ~/.vnc/xstartup
echo "autocutsel &" >> ~/.vnc/xstartup
