package r01f.types.url.web;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;



/**
 * Encapsulates all html link window opening mode info
 * Usage:
 * <pre class='brush:java'>
 * 		// Custom opening mode
 * 		HtmlLinkWindowOpeningMode openingMode = HtmlLinkWindowOpeningMode.create()
 * 														.withName("My new window")
 * 														.withAppearance(OpeningWindowAppearance.create(CENTERED)
 * 																				.withDimensions(800,600)
 * 																				.notResizable()
 * 																				.withBars(OpeningWindowBars.create()
 * 																								.showingLocationBar()
 * 																								.showingMenuBar()
 * 																								.hidingStatusBar()
 * 																								.hidingScrollBars()));
 * 		// Using templates
 * 		HtmlLinkWindowOpeningMode openingMode = HtmlLinkWindowOpeningMode.forOpenNewWindowCentered();
 * </pre>
 */
@MarshallType(as="openingData")
@ConvertToDirtyStateTrackable
@Accessors(prefix="_")
public class WebLinkWindowOpeningMode
  implements Serializable,
  			 Debuggable {

	private static final long serialVersionUID = 5828844380339964547L;
/////////////////////////////////////////////////////////////////////////////////////////
// 	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * window name
	 */
	@MarshallField(as="name",escape=true)
	@Getter @Setter private String _name = "r01NewWindow";
	/**
	 * If the window is opened in a NEW window this field contains tha appearance
	 * If the window is opened in the SAME window, this field is null
	 */
	@MarshallField(as="appearance")
	@Getter @Setter private OpeningWindowAppearance _appearance;
	/**
	 * Has the link an associated icon to hint that it's opened in a new window?
	 */
	@MarshallField(as="showNewWindowIcon")
	@Getter @Setter private boolean _showNewWindowIcon = false;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkWindowOpeningMode() {
		// default no-args constructor
	}
	public WebLinkWindowOpeningMode(final String name) {
		_name = name;
	}
	public WebLinkWindowOpeningMode(final String name,final OpeningWindowAppearance appearance) {
		_name = name;
		_appearance = appearance;
	}
	public WebLinkWindowOpeningMode(final String name,final OpeningWindowAppearance appearance,
									final boolean showNewWindowIcon) {
		_name = name;
		_appearance = appearance;
		_showNewWindowIcon = showNewWindowIcon;
	}
	public WebLinkWindowOpeningMode(final WebLinkWindowOpeningMode other) {
		_name = Strings.isNOTNullOrEmpty(other.getName()) ? new String(other.getName()) : "r01NewWindow";
		_appearance = other.getAppearance() != null ? new OpeningWindowAppearance(other.getAppearance()) : null;
		_showNewWindowIcon = other.isShowNewWindowIcon();
	}
	public static WebLinkWindowOpeningMode create() {
		WebLinkWindowOpeningMode outOpening = new WebLinkWindowOpeningMode();
		return outOpening;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public WebLinkWindowOpeningMode withAppearance(final OpeningWindowAppearance apparance) {
		_appearance = apparance;
		return this;
	}
	public WebLinkWindowOpeningMode showingNewWindowIcon() {
		_showNewWindowIcon = true;
		return this;
	}
	public WebLinkWindowOpeningMode withName(final String windowName) {
		_name = windowName;
		return this;
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	AUX TYPES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Window opening mode
	 */
	public static enum OpeningWindowMode {
		SIMPLE,
		CENTERED,
		MAXIMIZED,
		CUSTOMIZED;
	}
	@MarshallType(as="openingWindowApearance")
	@ConvertToDirtyStateTrackable
	@Accessors(prefix="_")
	public static class OpeningWindowAppearance
			 implements Serializable {
		private static final long serialVersionUID = 5359408350581058949L;

		@MarshallField(as="mode",		whenXml=@MarshallFieldAsXml(attr=true)) @Getter @Setter private OpeningWindowMode _openingMode = OpeningWindowMode.SIMPLE;		// window type
		@MarshallField(as="width",		whenXml=@MarshallFieldAsXml(attr=true)) @Getter @Setter private int _width = 800;			// width
		@MarshallField(as="height",		whenXml=@MarshallFieldAsXml(attr=true)) @Getter @Setter private int _height = 600;			// Height
		@MarshallField(as="x",			whenXml=@MarshallFieldAsXml(attr=true)) @Getter @Setter private int _x = 0;					// X position from the upper-left corner
		@MarshallField(as="y",			whenXml=@MarshallFieldAsXml(attr=true)) @Getter @Setter private int _y = 0;					// Y position from the upper-left corner
		@MarshallField(as="resizable",	whenXml=@MarshallFieldAsXml(attr=true)) @Getter @Setter private boolean _resizable = true;	// Can the window be resized?
		@MarshallField(as="bars")		@Getter @Setter private OpeningWindowBars _bars = new OpeningWindowBars();					// window bars

		public OpeningWindowAppearance() {
			// default no-args constructor
		}
		public OpeningWindowAppearance(final OpeningWindowAppearance other) {
			_openingMode = other.getOpeningMode();
			_width = other.getWidth();
			_height = other.getHeight();
			_x = other.getX();
			_y = other.getY();
			_bars = other.getBars() != null ? new OpeningWindowBars(other.getBars()) : null;
		}
		public static OpeningWindowAppearance create(final OpeningWindowMode mode,
													 final boolean resizable) {
			OpeningWindowAppearance outAppearance = new OpeningWindowAppearance();
			outAppearance.setOpeningMode(mode);
			outAppearance.setResizable(resizable);
			return outAppearance;
		}
		public OpeningWindowAppearance withDimensions(final int width,final int height) {
			_width = width;
			_height = height;
			return this;
		}
		public OpeningWindowAppearance locatedAt(final int x,final int y) {
			if (_openingMode != OpeningWindowMode.CENTERED) {
				_x = x;
				_y = y;
			}
			return this;
		}
		public OpeningWindowAppearance resizable() {
			_resizable = true;
			return this;
		}
		public OpeningWindowAppearance notResizable() {
			_resizable = false;
			return this;
		}
		public OpeningWindowAppearance withBars(final OpeningWindowBars bars) {
			_bars = bars;
			return this;
		}
	}
	@ConvertToDirtyStateTrackable
	@MarshallType(as="openingWindowBars")
	@Accessors(prefix="_")
	public static class OpeningWindowBars
			 implements Serializable {
		private static final long serialVersionUID = -3991014897290734846L;

		@MarshallField(as="showLocationBar",whenXml=@MarshallFieldAsXml(attr=true))		@Getter @Setter private boolean _showLocationBar = false;	// Show the url writing bar?
		@MarshallField(as="showMenuBar",	whenXml=@MarshallFieldAsXml(attr=true))		@Getter @Setter private boolean _showMenuBar = false;		// Show the menu bar?
		@MarshallField(as="showStatusBar",  whenXml=@MarshallFieldAsXml(attr=true))		@Getter @Setter private boolean _showStatusBar = false;		// Show the status bar?
		@MarshallField(as="showToolsBar",   whenXml=@MarshallFieldAsXml(attr=true))		@Getter @Setter private boolean _showTooslBar = false;		// Show the toolbar?
		@MarshallField(as="showScrollsBar", whenXml=@MarshallFieldAsXml(attr=true))		@Getter @Setter private boolean _showScrollBars = true;		// Show the scroll bars?

		public OpeningWindowBars() {
			// default no-args constructor
		}
		public OpeningWindowBars(final OpeningWindowBars other) {
			_showLocationBar = other.isShowLocationBar();
			_showMenuBar = other.isShowMenuBar();
			_showStatusBar = other.isShowStatusBar();
			_showTooslBar = other.isShowTooslBar();
			_showScrollBars = other.isShowScrollBars();
		}
		public static OpeningWindowBars create() {
			OpeningWindowBars outBars = new OpeningWindowBars();
			return outBars;
		}
		public OpeningWindowBars showingLocationBar() {
			_showLocationBar = true;
			return this;
		}
		public OpeningWindowBars hidingLocationBar() {
			_showLocationBar = false;
			return this;
		}
		public OpeningWindowBars showingMenuBar() {
			_showMenuBar = true;
			return this;
		}
		public OpeningWindowBars hidingMenuBar() {
			_showMenuBar = false;
			return this;
		}
		public OpeningWindowBars showingStatusBar() {
			_showStatusBar = true;
			return this;
		}
		public OpeningWindowBars hidingStatusBar() {
			_showStatusBar = false;
			return this;
		}
		public OpeningWindowBars showingScrollBars() {
			_showScrollBars = true;
			return this;
		}
		public OpeningWindowBars hidingScrollBars() {
			_showScrollBars = false;
			return this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
// TEMPLATES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * New 800x600 centered window
	 */
	public static WebLinkWindowOpeningMode forOpenNewWindowCentered() {
		WebLinkWindowOpeningMode outFeatures = new WebLinkWindowOpeningMode();

		OpeningWindowAppearance appearance = new OpeningWindowAppearance();
		appearance.setOpeningMode(OpeningWindowMode.CENTERED);

		outFeatures.setAppearance(appearance);
		return outFeatures;
	}
	/**
	 * New maximized window
	 */
	public static WebLinkWindowOpeningMode forOpenNewWindowMaximized() {
		WebLinkWindowOpeningMode outFeatures = new WebLinkWindowOpeningMode();

		OpeningWindowAppearance appearance = new OpeningWindowAppearance();
		appearance.setOpeningMode(OpeningWindowMode.MAXIMIZED);

		outFeatures.setAppearance(appearance);
		return outFeatures;
	}
	/**
	 * New customized window
	 */
	public static WebLinkWindowOpeningMode forOpenNewWindowCustomized() {
		WebLinkWindowOpeningMode outFeatures = new WebLinkWindowOpeningMode();

		OpeningWindowAppearance appearance = new OpeningWindowAppearance();
		appearance.setOpeningMode(OpeningWindowMode.CUSTOMIZED);
		appearance.getBars().setShowStatusBar(true);
		appearance.getBars().setShowTooslBar(true);
		outFeatures.setAppearance(appearance);

		return outFeatures;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String debugInfo() {
		StringBuilder appearanceDbg = null;
		StringBuilder barsDbg = null;
		if (_appearance != null) {
			appearanceDbg = new StringBuilder();
			appearanceDbg.append(Strings.customized("\n\tOpeningWindow Mode: {}",this.getAppearance().getOpeningMode()));
			appearanceDbg.append(Strings.customized("\n\t\t	   Resizable: {}",this.getAppearance().isResizable()));
			appearanceDbg.append(Strings.customized("\n\t\t	  Dimensions: {}x{}",this.getAppearance().getWidth(),
					  											  			   this.getAppearance().getHeight()));
			appearanceDbg.append(Strings.customized("\n\t\t		  Position: {},{}",this.getAppearance().getX(),
				  																 this.getAppearance().getY()));
			if (this.getAppearance().getBars() != null) {
				barsDbg = new StringBuilder();
				barsDbg.append("\n\t			  Bars:");
				barsDbg.append(Strings.customized("\n\t\tShow Location bar: {}",this.getAppearance().getBars().isShowLocationBar()));
				barsDbg.append(Strings.customized("\n\t\t	Show Menu bar: {}",this.getAppearance().getBars().isShowMenuBar()));
				barsDbg.append(Strings.customized("\n\t\t  Show Status bar: {}",this.getAppearance().getBars().isShowStatusBar()));
				barsDbg.append(Strings.customized("\n\t\t Show Scroll bars: {}",this.getAppearance().getBars().isShowScrollBars()));
				barsDbg.append(Strings.customized("\n\t\t  Show Tools bars: {}",this.getAppearance().getBars().isShowTooslBar()));
			}
		}
		return new StringBuilder()
					  .append(Strings.customized("\n\t			   Name: {}",_name))
					  .append(Strings.customized("\n\tShow newWindow icon: {}",_showNewWindowIcon))
					  .append(appearanceDbg)
					  .append(barsDbg)
					  .toString();
	}
}
