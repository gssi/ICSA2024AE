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
The backend is composed of 2 main services: 1) A generator for the reference architecture that can generate the graphical representation of the RA and also check whether a concrete architecture conforms to the selected RA; 2) a generator translating the concrete architecture into a graphical version, checking also if the modeled architecture is compliant to the given constraints and finally generates the dashboard charts and information about the conformance.

To launch the services, run these maven commands that will expose 2 services on the ports 8001 and 8002:

mvn -X function:run -Drun.functionTarget=it.cs.gssi.ramodeling.web.RA2HTML -Drun.port=8001

mvn -X function:run -Drun.functionTarget=it.cs.gssi.ramodeling.web.MyA2HTML -Drun.port=8002

These services will be called by the frontend via ajax calls.

## Frontend
The frontend folder contains a web app communicating with the backend services. The frontend app is simply an HTML page loading a selected RA and making calls to the backend services. The exchange of models is applied using a lightweight format called Flexmi (https://eclipse.dev/epsilon/doc/flexmi/). The backend services respond with JSON format of the required information. By selecting a RA from the left menu will preload a reference architecture from the server and will use it for the entire modeling process. The persistency of the architectural models modeled by the user is still unimplemented and currently, we offer some exemplary models pre-loaded from client-side. 
