package website.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;

@SupportsInformalParameters
public class Image {

	@Parameter
	private String path;
	
	@Inject
    private ComponentResources resources;
	
	@BeginRender
	public void onBeginRender(MarkupWriter writer){
		Element el = writer.element("img", "src","/assets/"+path);
		resources.renderInformalParameters(writer);
		writer.end();
	}
}
