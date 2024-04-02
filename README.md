
### Deployment of Large Scale System with Kubernetes on Google Cloud ###
Deploy a large-scale system utilizing Python Django for the Webapp, Java Spring Boot for the RESTful service, PostgreSQL and Cassandra for the database, ReactJS for the SPA(Single Page Application).

Containerization is achieved with Docker containers and Docker Compose, service discovery handled by Netflix Eureka and a gateway provided by NetflixZuul.


![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/41.PNG)

Client-side load balancing is managed using Ribbon, while server-side load balancing is facilitated by Nginx.

Logging is handled by Elasticsearch, Fluent, and Kibana, with tracing supported by Uber Jaeger, and monitoring and alerts provided by Prometheus.

Caching is managed using Redis, RabbitMQ facilitates asynchronous processing, and Cassandra handles horizontal data partitioning.

Deployment is on Kubernetes on GCP, enabling auto-scaling, high availability, and rolling upgrades.



### Getting Started ###

1. **Create a project**
   
   Choose name of your project.
   Involves setting a billing account.
   You can use 90-day trial to help cover costs of resources for this project.

   Search for 'Compute Engine' and select 'Create Instance'
   Enter name of VM instance, select Region, Zone and Machine Configuration(Low cost, day-to-day computing).
   
   Select Machine type 
   - 4 vCPUs, 2 core, 16 GB memory
   
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/39.png)

   Change availability policies to 'Spot' VM provisioning model.
   This reduces monthly estimate cost by more than 50%

   Each instance requires a disk to boot from. Select an image to create a new boot disk or attach an existing disk to the instance.
   
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/40.png)

   Then select 'Create' at bottom to provision new VM instance.
   Note the External IP assigned.


2. **Create a New User on VM Instance**
   
   Click on 'SSH' and allow SSH-in-browser to connect to devbuild-1 VM
   Second option for SSH connection as shown [here](https://www.youtube.com/watch?v=fmh94mNQHQc):
   
   - Generate your own private/public key pair, copy public key to settings of devbuild-1 instance and save private key on your local machine.
   - Connect to devbuild-1 instance using putty
   
   Change password for root user
   ```bash
   sudo passwd
   ```
   Switch to root user.
   Add new user to sudo group. In this case new user is 'odennav-admin'

   ```bash
   sudo adduser odennav-admin
   sudo usermod -aG sudo odennav-admin
   ```
   You'll be prompted to set a password and provide additional information about the new    
   user, such as full name, work phone, etc. This information is optional. Press 'Enter'   
   to skip each prompt.
    
   ```bash
   Test sudo privileges by switching to new user
   su - odennav-admin
   sudo ls /root
   ```

   You'll notice prompt to enter your user password.
   To disable this prompt for every sudo command, implement the following:

   Add sudoers file for odennav-admin
   ```bash
   cd /etc/sudoers.d/
   echo "odennav-admin ALL=(ALL) NOPASSWD: ALL" > odennav-admin
   ```
   Set permissions for sudoers file
   ```bash
   chmod 0440 odennav-admin
    ```

3. **Confirm Git is installed**
   ```bash
   git version
   ```

   If not available, install the package
   ```bash
   sudo apt update
   sudo apt install git -y
   git version
   ```

   Clone git repo codebase
   ```bash
   git clone https://github.com/odennav/large-scale-app-micros-gcp.git
   ```
4. **Complete Image Build of System Components**
   
   Enable execution of all bash scripts in large-scale-app-micros-gcp/ directory
   ```bash
   cd large-scale-app-micros-gcp/
   sudo find ~/large-scale-app-micros-gcp/ -type f -name "*.sh" -exec sudo chmod +x {} \;
   ```

   Install docker and docker-compose
   ```bash
   cd large-scale-app-micros-gcp/bin
   ./install-docker.sh
   ```
   
   Confirm docker is installed
   ```bash
   docker version
   ```
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/4.png)

   Trigger a complete build and build the entire codebase.
   -Building codebase
   -Staging them to a stage directory
   -Building docker images required
   ```bash
   ./build-system.sh
   ```

5. **Setup Container Registry and Push images to Google Cloud Registry**
   
   Before we can upload created images to google cloud registry, we have to authorize host docker in devbuild-1 VM instance.
   ```bash
   cd ~/large-scale-app-micros-gcp/bin
   ./gcp-authorize-docker.sh
   ```
   During remote authentication to gcloud CLI, you'll be asked to copy link in your browser because Google SDK wants to access your google account.     Allow access, copy authorization code and paste in SSH-in-browser.

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/2.png)
   It locates all images starting with 'ntw', then tags them before pushing to registry
   Ensure registry zone in script is the same region as your devbuild-1 instance and is also a region in either US, Europe or Asia.

   REGISTRY_HOST=eu.gcr.io due to VM instance and Kubernetes cluster provisioned in European region.

   ```bash
   cd ~/large-scale-app-micros-gcp/kubernetes
   ./gcp-push-images.sh
   ```
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/5.png)

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/6.png)

6. **Create K8s Cluster on Google Cloud**

   Go to 'Compute' section and click 'Kubernetes Engine'.
   Enable Kubernetes API
   Click on 'CREATE' under section of Kubernetes clusters.
   Choose 'GKE Standard' or switch to 'STANDARD CLUSTER'.
   Name your kubernetes cluster and select 'Regional' for Location type, to have our system in multiple zones within a region.
   Select same region for devbuild-1 VM instance

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/7.png)

   Change number of nodes per zone to 1 in 'Node pool details', so we have 3 machines in different zones.
   
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/8.png)

   For each node in pool,
   - Select E2 machine type
   - 6 vCPU, 2 cores each
   - 12GB memory total
   - Boot disk size of 50GB
   - Enable nodes on spot VMs(reduces monthly cost)
   
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/9.png)


   Click 'CREATE' at bottom to start process of creating kubernetes cluster
   Note cluster created with total of 18 vCPUs, 36GB memory and nodes from each zone.


   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/11.png)
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/10.png)


7. **Kubernetes Environment Configuration for System**
   
   To host our system on this gcp k8s cluster, we'll have to create k8s config and services.

   Install kubectl
   kubectl enables devbuild-1 VM instance to communicate with the kubernetes cluster
   ```bash
   cd ~/large-scale-app-micros-gcp/kubernetes
   ./gcp-install-kubectl.sh
   ```

   Configure kubectl command line access to k8s cluster
   Locate your cluster created in 'Kubernetes Engine' section on GCP and click on ':'  to view options, then click 'Connect'.
   Copy and paste gcloud command to devbuild-1 terminal.

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/12.png)

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/13.png) 
   
   Confirm kubectl has access to nodes in pool
   ```bash
   kubectl get nodes
   ```
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/14.png)

   Create namespaces
   ```bash
   cd ~/large-scale-app-micros-gcp/kubernetes/config/0-env
   kubectl apply -f 0-namespaces.yaml
   ```

   Apply config map
   Set environment variables for each component in system
   ```bash
   cd ~/large-scale-app-micros-gcp/kubernetes/config/0-env
   kubectl apply -f 1-config-map.yaml
   ```

   Apply secrets config
   Set environment variables for secrets to postgresql databases
   ```bash
   cd ~/large-scale-app-micros-gcp/kubernetes/config/0-env
   kubectl apply -f 2-secrets-map.yaml
   ```
   You can delete 2-secrets.yaml file after secrets resource is created and stored in k8s cluster.

   Apply resources limit
   Set CPU and Memory resource limits to pods created in services namespace
   ```bash
   cd ~/large-scale-app-micros-gcp/kubernetes/config/0-env
   kubectl apply -f 3-resources.yaml
   ```


8. **Kubernetes Volume Configuration**
   
   Configure persistent volumes for each node in node pool

   ```bash
   cd ~/large-scale-app-micros-gcp/kubernetes/volume
   ./kube-volumes.sh
   ```
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/15.png)

   Set up all System Workloads on Kubernetes Cluster
   Identify all config yaml files and create all resources required to start the app on kubernetes cluster

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/37.png)

   kube-deploy.sh script creates all services and deployments configured in the yaml files identified.
   Ensure variable REGISTRY_HOST=eu.gcr.io is set in bash script due to regional setup of clusters and registry 
   ```bash
   cd ~/large-scale-app-micros-gcp/kubernetes
   ./kube-deploy.sh
   ```

   Go to 'Workloads' section under 'Kubernetes Engine' and view pods created and running.

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/18.png)

   Confirm all namespaces have been created
   ```bash
   kubectl get ns
   ```
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/25.png)

   Check for webapp and spa services in ui namespace

   ```bash
   kubectl get svc -n ui
   ```
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/26.png)

9. **Access both Webapp and SPA**
   
   Create 'allow-ssh' firewall rule in your vpc network settings.
   Search for 'VPC Network' in Google cloud platform and click on 'Firewall' on left-side bar. Select 'CREATE FIREWALL RULE'.
   - Enter name of firewall rule - 'allow-sys'
   - Direction of traffic is 'Ingress'
   - Use '0.0.0.0/0' as source of IPv4 ranges
   - Under section for 'Protocol and ports' select 'TCP' and insert Nodeports configured for both webapp and spa, 32100 and 32105.
   
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/28.png)

   - Click on 'CREATE' at bottom to create VPC firewall rule.

   Access Login page from your browser on local machine.
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/24.png)

   Please note this option opens ports 32100 and 32105 on all VM instances in your VPC network.
   
   Effectively, you can select the firewall rules initially created for each of the kubernetes cluster nodes and edit firewall settings by including    Nodeports among TCP ports allowed.

   **Access System Components for Centralized Logging, Tracing and Resource Monitoring**
   
   Add the following Nodeports as TCP ports in firewall rule created above
   - 32101 for Kibana/Elasticsearch 
   
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/30.png)

   - 32102 for Uber Jaeger

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/31.png)

   - 32103 for Prometheus
   
   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/33.png)

   - 32104 for RabbitMQ

   ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/36.png)

10. **Shutdown Kubernetes Cluster**
    
    To remove all resources for kubernetes cluster and stop incurring charges. 
    
    ```bash
    cd ~/large-scale-app-micros-gcp/kubernetes
    ./kube-deploy.sh ./config/ delete
    ```
    Delete persistent volumes created
    
    ```bash
    cd ~/large-scale-app-micros-gcp/kubernetes
    ./kube-volumes.sh delete
    ```

    Go to 'Clusters' section in 'Kubernetes Engine' product and delete cluster.
    Click on Actions ':' menu and select 'Delete'

    ![](https://github.com/odennav/large-scale-system-micros-gcp/blob/main/snip/deployment-snips/38.png)


## Special Credits

Special thanks to [Anurag Yadav](https://www.newtechways.com/).


### Contributions ###

* Writing tests
* Code review

Enjoy!
