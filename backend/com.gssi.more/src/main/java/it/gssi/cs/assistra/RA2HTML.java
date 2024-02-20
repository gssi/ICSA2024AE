package it.cs.gssi.ramodeling.web;

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
import java.util.Set;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;
import org.eclipse.epsilon.evl.EvlModule;
import com.google.gson.JsonObject;
import java.io.PrintWriter;


public class RA2HTML extends EpsilonLiveFunction {

	public static void main(String[] args) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JsonObject response = null;
		String raFlexmi = Files.readString(Paths.get(new File("src/main/resources/models/iot.flexmi").toURI()));
		String evl= Files.readString(Paths.get(new File("src/main/resources/models/pub-sub.evl").toURI()));
		
		System.out.println(
				new RA2HTML().
				run(raFlexmi));
				//runRAValidation(raFlexmi, evl, bos, response));
	}
	
	@Override
	public void serviceImpl(JsonObject request, JsonObject response) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Collection<UnsatisfiedConstraint> validationresults = null;
		Instant start = Instant.now();
		
		String raFlexmi = "?nsuri: http://it.disim.univaq/architecturemodeling/v2\n"
				+ "\n";
		String html ="";
		if(request.get("mode")==null) {
		 raFlexmi = 
			Files.readString(Paths.get(new File("src/main/resources/models/"+request.get("ra").getAsString()).toURI()));
		 html = run(raFlexmi);
		}else {
		 raFlexmi = raFlexmi+request.get("myRA").getAsString();
		 String evl= Files.readString(Paths.get(new File("src/main/resources/models/pub-sub.evl").toURI()));
			
		validationresults = runRAValidation(raFlexmi, request.get("evlScript").getAsString(), bos, response);
		 
		 	html = run(raFlexmi);
			
		 
		}
		
		
		response.addProperty("RADiagram", html);
		
		
		Instant end = Instant.now();
		String exectime = "Code gen: "+Duration.between(start,end).toMillis()+" ms";
		if(validationresults!=null && validationresults.size()>0)response.addProperty("errors", validationresults.toString()+"\n");
		response.addProperty("output",(exectime+"\n")+ bos.toString());
	}
	
	public Collection<UnsatisfiedConstraint> runRAValidation( String RAFlexmi, String evl, OutputStream outputStream, JsonObject response)  {
		Set<UnsatisfiedConstraint> constraints=null;
		try {
			IEolModule module = new EvlModule();
			module.parse(evl);
			//module.parse(new File("src/main/resources/validate.evl"));
			
			if (!module.getParseProblems().isEmpty()) {
				
				System.out.println(module.getParseProblems().get(0).toString());
							
			}
			
			String raEmfatic =  Files.readString(Paths.get(new File("src/main/resources//metamodels/moremm.emf").toURI()));

			
			module.getContext().setOutputStream(new PrintStream(outputStream));
			
			//return runEvl((EvlModule) module, kpiFlexmi, kpiEmfatic, scFlexmi, scEmfatic, response); 
			
			InMemoryEmfModel ramodel = getInMemoryFlexmiModel(RAFlexmi, raEmfatic);
			
			ramodel.setName("M");
			
			module.getContext().getModelRepository().addModel(ramodel);
		
			 constraints = (Set<UnsatisfiedConstraint>) module.execute();
		
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				System.out.println(exceptionAsString);
			}
			
			return  constraints;	
			
		}
	
	
	protected String run(String flexmi) throws Exception {
		String raEmfatic =  Files.readString(Paths.get(new File("src/main/resources/metamodels/moremm.emf").toURI()));
				
		
		return run(getInMemoryFlexmiModel(flexmi, raEmfatic));
	}
	
	protected String run(InMemoryEmfModel model, Variable... variables) throws Exception {
		
		EglTemplateFactoryModuleAdapter module = new EglTemplateFactoryModuleAdapter();
		module.parse(new File("src/main/resources/RA2html.egl"));
		model.setName("M");
	
		module.getContext().getModelRepository().addModel(model);
		module.getContext().getFrameStack().put(variables);
		
		return module.execute() + "";
	}

}
