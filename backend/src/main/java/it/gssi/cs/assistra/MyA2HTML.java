package it.gssi.cs.assistra;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfatic.core.EmfaticResourceFactory;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;
import org.eclipse.epsilon.flexmi.FlexmiResourceFactory;

import com.google.gson.JsonObject;

public class MyA2HTML extends EpsilonLiveFunction {

	public static void main(String[] args) throws Exception {
		String myAFlexmi = Files.readString(Paths.get(new File("src/main/resources/models/fiware.flexmi").toURI()));
		String raFlexmi = Files.readString(Paths.get(new File("src/main/resources/models/iot.flexmi").toURI()));
		/*System.out.println(
				new MyA2HTML().
				run(myAFlexmi));*/
		System.out.println("*********");
		System.out.println(new MyA2HTML().validate(raFlexmi,myAFlexmi,System.out, new JsonObject()));
		//System.out.println(new MyA2HTML().genCharts(myAFlexmi, raFlexmi));
	}
	
	@Override
	public void serviceImpl(JsonObject request, JsonObject response) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		Instant start = Instant.now();
		
		String validationmsg ="";
		String nsuri = "http://it.disim.univaq/architecturemodeling/v2";
		String myAFlexmi = "?nsuri: "+nsuri+"\n"+ request.get("myAFlexmi").getAsString();
		
		String raFlexmi = Files.readString(Paths.get(new File("src/main/resources/models/"+request.get("ra").getAsString()).toURI()));
		
		String html = run(myAFlexmi);
		
		response.addProperty("myADiagram", html);
		
		
		IEolModule evlmodule = new EvlModule();
		evlmodule.parse(request.get("evl").getAsString());
		//module.parse(new File("src/main/resources/validate.evl"));
		
		if (!evlmodule.getParseProblems().isEmpty()) {
			
			validationmsg+="error:"+(evlmodule.getParseProblems().get(0).toString());
						
		}
		
		Collection<UnsatisfiedConstraint> evlValidation = validateScript(request.get("evl").getAsString(), myAFlexmi, raFlexmi,System.out, response);
		
		String htmlCharts = genCharts(myAFlexmi, raFlexmi);
		
		response.addProperty("myCharts", htmlCharts);
		
		Instant end = Instant.now();
		String exectime = "Code gen: "+Duration.between(start,end).toMillis()+" ms";
		Collection<UnsatisfiedConstraint> validationmsgs = validate(raFlexmi,myAFlexmi,System.out, new JsonObject());
		
		
		if(validationmsgs!=null || evlValidation!=null ) {
		if(validationmsgs!=null) {
		for (Iterator iterator = validationmsgs.iterator(); iterator.hasNext();) {
				UnsatisfiedConstraint unsatisfiedConstraint = (UnsatisfiedConstraint) iterator.next();
				
				if(unsatisfiedConstraint.getConstraint().isCritique()) {
				
					validationmsg+="critique:"+unsatisfiedConstraint.getMessage()+"|";
				}else {
					validationmsg+="error:"+unsatisfiedConstraint.getMessage()+"|";
				}
			}
		}
		if(evlValidation!=null) {
		for (Iterator iterator = evlValidation.iterator(); iterator.hasNext();) {
			
			UnsatisfiedConstraint unsatisfiedConstraint = (UnsatisfiedConstraint) iterator.next();
			
			if(unsatisfiedConstraint.getConstraint().isCritique()) {
			
				validationmsg+="critique:"+unsatisfiedConstraint.getMessage()+"|";
			}else {
				validationmsg+="error:"+unsatisfiedConstraint.getMessage()+"|";
			}
			
		}
		}
		
		}else {
			validationmsg+="validation complete!";
		}
		response.addProperty("output", validationmsg +"\n"+
				exectime+""+ bos.toString()+"\n");
		
	}
	

	private Set<UnsatisfiedConstraint> validateScript(String evl, String myAFlexmi, String raFlexmi, OutputStream outputStream, JsonObject response) {
		// TODO Auto-generated method stub
		Set<UnsatisfiedConstraint> constraints=null;
		try {
			IEolModule module = new EvlModule();
			
			module.parse(evl);
			
			if (!module.getParseProblems().isEmpty()) {
				
				System.out.println(module.getParseProblems().get(0).toString());
							
			}

			
			module.getContext().setOutputStream(new PrintStream(outputStream));
			
	        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("flexmi", new FlexmiResourceFactory());
	        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emf", new EmfaticResourceFactory());
	        
			String ADLEmfatic =  Files.readString(Paths.get(new File("src/main/resources/metamodels/moremm.emf").toURI()));
			
			ResourceSet resourceSet = new ResourceSetImpl();
			EPackage ePackage = getEPackage(ADLEmfatic);
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			
			Resource RAresource = resourceSet.createResource(URI.createURI("ra.flexmi"));
			
			RAresource.load(new ByteArrayInputStream(raFlexmi.getBytes()), null);
			//resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new FlexmiResourceFactory());
			Resource resource = resourceSet.createResource(URI.createURI("flexmi.flexmi"));
			
			
			resource.load(new ByteArrayInputStream(myAFlexmi.getBytes()), null);
			
			InMemoryEmfModel model = new InMemoryEmfModel(resource);
			InMemoryEmfModel ramodel = new InMemoryEmfModel(RAresource);
			
			model.setName("M");
			ramodel.setName("RA");
			module.getContext().getModelRepository().addModel(model);
			module.getContext().getModelRepository().addModel(ramodel);
			 
		
			constraints = (Set<UnsatisfiedConstraint>) module.execute();
			
			for (Iterator iterator = constraints.iterator(); iterator.hasNext();) {
				UnsatisfiedConstraint unsatisfiedConstraint = (UnsatisfiedConstraint) iterator.next();
				//System.out.println(unsatisfiedConstraint.getInstance());
			}
			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				StringWriter sw = new StringWriter();
				
				String exceptionAsString = sw.toString();
				System.out.println(exceptionAsString);
			}
			
			return  constraints;	

	}

	public Collection<UnsatisfiedConstraint> validate( String flexmiramodel, String flexmimyAmodel, OutputStream outputStream, JsonObject response)  {
		Set<UnsatisfiedConstraint> constraints=null;
		try {
			IEolModule module = new EvlModule();
			
			module.parse(new File("src/main/resources/validate2.evl"));
			
			if (!module.getParseProblems().isEmpty()) {
				
				System.out.println(module.getParseProblems().get(0).toString());
							
			}

			
			module.getContext().setOutputStream(new PrintStream(outputStream));
			
	        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("flexmi", new FlexmiResourceFactory());
	        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emf", new EmfaticResourceFactory());
	        
			String ADLEmfatic =  Files.readString(Paths.get(new File("src/main/resources/metamodels/moremm.emf").toURI()));
			
			ResourceSet resourceSet = new ResourceSetImpl();
			EPackage ePackage = getEPackage(ADLEmfatic);
			resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			
			Resource RAresource = resourceSet.createResource(URI.createURI("ra.flexmi"));
			
			RAresource.load(new ByteArrayInputStream(flexmiramodel.getBytes()), null);
			//resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new FlexmiResourceFactory());
			Resource resource = resourceSet.createResource(URI.createURI("flexmi.flexmi"));
			
			
			resource.load(new ByteArrayInputStream(flexmimyAmodel.getBytes()), null);
			
			InMemoryEmfModel model = new InMemoryEmfModel(resource);
			InMemoryEmfModel ramodel = new InMemoryEmfModel(RAresource);
			
			model.setName("M");
			ramodel.setName("RA");
			module.getContext().getModelRepository().addModel(model);
			module.getContext().getModelRepository().addModel(ramodel);
			 
		
			constraints = (Set<UnsatisfiedConstraint>) module.execute();
			
			for (Iterator iterator = constraints.iterator(); iterator.hasNext();) {
				UnsatisfiedConstraint unsatisfiedConstraint = (UnsatisfiedConstraint) iterator.next();
				//System.out.println(unsatisfiedConstraint.getInstance());
			}
			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				StringWriter sw = new StringWriter();
				
				String exceptionAsString = sw.toString();
				System.out.println(exceptionAsString);
			}
			
			return  constraints;	
			
		}
	
	private String genCharts(String myAFlexmi, String raFlexmi) throws Exception{
		// TODO Auto-generated method stub
		String ADLEmfatic =  Files.readString(Paths.get(new File("src/main/resources/metamodels/moremm.emf").toURI()));
		//String RAEmfatic =  Files.readString(Paths.get(new File("src/main/resources/metamodels/ra.emf").toURI()));
		
		return genHTMLCharts(myAFlexmi,raFlexmi);
	}
	
	protected String run(String flexmi) throws Exception {
		String ADLEmfatic =  Files.readString(Paths.get(new File("src/main/resources/metamodels/moremm.emf").toURI()));
		//String RAEmfatic =  Files.readString(Paths.get(new File("src/main/resources/metamodels/ra.emf").toURI()));
		
		return run(getInMemoryFlexmiModel(flexmi, ADLEmfatic));
	}
	
	protected String run(InMemoryEmfModel model, Variable... variables) throws Exception {
		
		EglTemplateFactoryModuleAdapter module = new EglTemplateFactoryModuleAdapter();
		module.parse(new File("src/main/resources/MyA2html.egl"));
		model.setName("M");
		//ramodel.setName("RA");
		module.getContext().getModelRepository().addModel(model);
		//module.getContext().getModelRepository().addModel(ramodel);
		module.getContext().getFrameStack().put(variables);
		
		return module.execute() + "";
	}
	
protected String genHTMLCharts(String flexmimyAmodel, String flexmiramodel,Variable... variables) throws Exception {
		
		EglTemplateFactoryModuleAdapter module = new EglTemplateFactoryModuleAdapter();
		module.parse(new File("src/main/resources/MyA2Charts.egl"));
		
		
		/*model.setName("M");
		ramodel.setName("RA");
		module.getContext().getModelRepository().addModel(model);
		module.getContext().getModelRepository().addModel(ramodel);*/
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("flexmi", new FlexmiResourceFactory());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emf", new EmfaticResourceFactory());
        
		String ADLEmfatic =  Files.readString(Paths.get(new File("src/main/resources/metamodels/moremm.emf").toURI()));
		
		ResourceSet resourceSet = new ResourceSetImpl();
		EPackage ePackage = getEPackage(ADLEmfatic);
		resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		
		Resource RAresource = resourceSet.createResource(URI.createURI("ra.flexmi"));
		
		RAresource.load(new ByteArrayInputStream(flexmiramodel.getBytes()), null);
		//resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new FlexmiResourceFactory());
		Resource resource = resourceSet.createResource(URI.createURI("flexmi.flexmi"));
		
		
		resource.load(new ByteArrayInputStream(flexmimyAmodel.getBytes()), null);
		
		InMemoryEmfModel model = new InMemoryEmfModel(resource);
		InMemoryEmfModel ramodel = new InMemoryEmfModel(RAresource);
		
		model.setName("M");
		ramodel.setName("RA");
		module.getContext().getModelRepository().addModel(model);
		module.getContext().getModelRepository().addModel(ramodel);
		module.getContext().getFrameStack().put(variables);
		
		return module.execute() + "";
	}

}
