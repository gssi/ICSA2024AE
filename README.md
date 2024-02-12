# ICSA2024AE
Public repository for the ICSA 2024 Paper Artifact Evaluation
# Backend services
To launch the services:

mvn -X function:run -Drun.functionTarget=it.cs.gssi.ramodeling.web.RA2HTML -Drun.port=8001

mvn -X function:run -Drun.functionTarget=it.cs.gssi.ramodeling.web.MyA2HTML -Drun.port=8002

# Frontend
The frontend folder contains a web app communicating with the backend services.
