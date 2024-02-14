# ICSA 2024 Artifact Evaluation Track
## Continuous Conformance of Software Architectures
- Alessio Bucaioni (Mälardalen University, Västerås, Sweden)
- Amleto Di Salle (Gran Sasso Science Institute, L'Aquila, Italy)
- Ludovico Iovino (Gran Sasso Science Institute, L'Aquila, Italy)
- Leonardo Mariani (University of Milano-Bicocca, Milan, Italy)
- Patrizio Pelliccione (Gran Sasso Science Institute, L'Aquila, Italy)

## Abstract
Software architectures are pivotal in the success of software-intensive systems and serve as foundational elements that significantly impact the overall software quality. Reference architectures abstract software elements, define main responsibilities and interactions within a domain, and guide the architectural design of new systems. Using reference architectures offers advantages like enhanced interoperability, cost reduction through reusability, decreased project risks, improved communication, and adherence to best practices. However, these benefits are most pronounced when software architectures align with reference architectures. Deviations from prescribed reference architectures can nullify these benefits. Uncontrolled misalignment can become prohibitively expensive, necessitating costly redevelopments, with maintenance costs reaching up to 90% of development costs. Conformance-checking processes and identifying and resolving violations in the software architecture are essential to mitigate misalignment. To address these challenges, we introduce the concept of continuous conformance that is expressed as a distance function, together with a process supporting it. Continuous conformance quantifies the degree to which a software architecture adheres to a designated reference architecture. The conformance concept enables multi-level, incremental, and non-blocking checking and restoration tasks and allows the check of partial architectures without obstructing the design process. We operationalize this process through an assistive modeling tool to architect an Internet of things-based system.

The following public repository contains 

## Systematic Literature Review Replication package
The slr-replication-package folder contains the replication package related to the research process and the evaluation sections.
The folder structure is as follows:
- the SLR folder contains the all the artefacts for the systematic literature review
- the file GR.xlsx summarises the grey literature review process
- the Pre-study folder contains the transcript from the elicitation interviews
- the Validation folder contains the transcript from the validation interviews


## Backend services
To launch the services:

mvn -X function:run -Drun.functionTarget=it.cs.gssi.ramodeling.web.RA2HTML -Drun.port=8001

mvn -X function:run -Drun.functionTarget=it.cs.gssi.ramodeling.web.MyA2HTML -Drun.port=8002

## Frontend
The frontend folder contains a web app communicating with the backend services.
