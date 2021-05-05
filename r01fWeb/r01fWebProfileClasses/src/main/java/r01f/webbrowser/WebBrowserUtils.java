package r01f.webbrowser;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import r01f.types.url.Url;
import r01f.types.url.UrlQueryString;
import r01f.util.types.StringEscapeUtils;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class WebBrowserUtils {
	/**
	 * Creates HTML for an auto-post form > an html page with a post form that's automatically submitted when the page is loaded
	 * @param writer  
	 * @param url
	 * @param values
	 * @throws IOException
	 */
	public static String composeAutoSubmitHtmlForm(final Url url,
										     	   final Map<String,String> values) {
		StringWriter sw = new StringWriter();
		WebBrowserUtils.composeAutoSubmitHtmlForm(sw,
												  url, 
												  values);
		return sw.toString();
	}
	/**
	 * Creates HTML for an auto-post form > an html page with a post form that's automatically submitted when the page is loaded
	 * @param writer
	 * @param url
	 * @param values
	 * @throws IOException
	 */
	@SneakyThrows
	public static void composeAutoSubmitHtmlForm(final Writer writer,
										   		 final Url url,
										   		 final Map<String,String> values) {
		writer.write(Strings.customized("<html>\n" + 
											"<head></head>\n" + 
											"<body>\n" +
											"<form id='r01-auto-submmitted-form' action='{}' method='POST'>",
										StringEscapeUtils.escapeHTML(url.asString())));
		for (Iterator<Map.Entry<String,String>> entriesIt = values.entrySet().iterator(); entriesIt.hasNext(); ) {
			Map.Entry<String,String> me = entriesIt.next();
			
			String encodedKey = StringEscapeUtils.escapeHTML(me.getKey()).toString();
			String encodedValue = StringEscapeUtils.escapeHTML(me.getValue()).toString();
			writer.write(Strings.customized("<input type='hidden' id='{}' name='{}' value='{}'/>{}",
										    encodedKey,encodedKey,encodedValue,
										    entriesIt.hasNext() ? "\n" : ""));
		}
		writer.write("</form>\n" +
					 "<script type='text/javascript'>\n" +
					 	"document.getElementById('r01-auto-submmitted-form')\n" +
					 			".submit();\n" +
					 "</script>\n" +
					 "</body>\n" +
					 "</html>");
	}
	/**
	 * Renders an HTTP response that will cause the browser to POST the specified values to an url.
	 * @param url the url where to perform the POST.
	 * @param response the {@link HttpServletResponse}.
	 * @param values the values to include in the POST.
	 * @throws IOException thrown if an IO error occurs.
	 */
	public static void postUsingBrowser(final HttpServletResponse response,
										final Url url,  
		  							    final Map<String,String> values) throws IOException {

		response.setContentType("text/html");
		
		@SuppressWarnings("resource")
		Writer writer = response.getWriter();
		WebBrowserUtils.composeAutoSubmitHtmlForm(writer,
											url,values);
		writer.flush();
	
		response.setHeader("Cache-Control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
  	}
	/**
	 * Redirects to the given url with the given params
	 * @param response
	 * @param url
	 * @param values
	 * @throws IOException
	 */
	public static void redirectWithParams(final HttpServletResponse response,
										  final Url url,
										  final Map<String,String> values) throws IOException {
		Url theUrl = CollectionUtils.hasData(values) ? url.joinWith(new UrlQueryString(values))
													 : url;
		response.sendRedirect(theUrl.asString());
	}
}
