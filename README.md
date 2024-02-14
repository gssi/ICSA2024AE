# ICSA 2024 Continuous Conformance of Software Architectures - Articact Evaluation Track
Public repository for the ICSA 2024 Paper Artifact Evaluation

# Systematic Literature Review Replication package
The slr-replication-package folder contains the replication package related to the research process and the evaluation sections.
The folder structure is as follows:
the SLR folder contains the all the artefacts for the systematic literature review
the file GR.xlsx summarises the grey literature review process
the Pre-study folder contains the transcript from the elicitation interviews
the Validation folder contains the transcript from the validation interviews



# Backend services
To launch the services:

mvn -X function:run -Drun.functionTarget=it.cs.gssi.ramodeling.web.RA2HTML -Drun.port=8001

mvn -X function:run -Drun.functionTarget=it.cs.gssi.ramodeling.web.MyA2HTML -Drun.port=8002

# Frontend
The frontend folder contains a web app communicating with the backend services.
