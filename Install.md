## Installation Guide 

This document gives the instructions to install the AssistRA tool, i.e., an assistive modeling tool supporting continuous conformance.


## System Requirements
The software requirements can be summarized as:

* Microsoft Windows, Mac OS X or Unix Operating Systems and derived are supported
* Latest OpenJDK 8 (or other Java distribution like Eclipse Adoptium https://adoptium.net/) is recommended. 
* Latest [Apache Maven](https://maven.apache.org/) installed.
* A web server like [Apache HTTP server](https://httpd.apache.org/) installed. For Mac OS or Windows, the [MAMP](https://www.mamp.info/) product contains a web server like Apache HTTP.


### Back-end services
To launch the services, run these maven commands that will expose two services on the ports 8001 and 8002:
1. Open a terminal in the directory where you downloaded the source code. Go to the `backend` folder and type the following command. 

    ```
    mvn clean compile
    ```

2. Open a terminal in the directory where you downloaded the source code. Go to the `backend` folder and type the following command. 


    ```
    mvn -X function:run -Drun.functionTarget=it.gssi.cs.assistra.RA2HTML -Drun.port=8001
    ```
3. Open a terminal in the directory where you downloaded the source code. Go to the `backend` folder and type the following command. 


    ```
    mvn -X function:run -Drun.functionTarget=it.gssi.cs.assistra.MyA2HTML -Drun.port=8002
    ```
	
## Front-end
To install the front-end, copy all files from `frontend` folder to the `www/assistra` folder. If the `assist` folder does not exist, create it.

Once front-end and back-end services are running and installed, open a browser and type [http://localhost/assistra](http://localhost/assistra)