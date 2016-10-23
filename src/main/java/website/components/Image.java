package website.components;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;

public class Image {

	@Parameter
	private String path;
	
	@BeginRender
	public void onBeginRender(MarkupWriter writer){
		Element el = writer.element("img", "src","/assets/"+path);
		writer.end();
	}
}
