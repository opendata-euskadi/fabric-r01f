package r01f.types.pager;

import java.io.Serializable;
import java.util.Iterator;

import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;


/**
 * Models a search results paging 
 * 
 * In order to use the {@link Paging} type the total number of search results must be known
 * Results are organized into pages of X items which are shown in a navigation bar that only 
 * shows a window of pages 
 * Example: If the total number of items of the search result is 230, there are 23 pages of 10 items
 *		  If the navigation bar shows a window of 5 pages like: 
 *				  << 1 2 3 4 5 >>
 * 			as the user is paging forward, the window of pages shown at the navigation bar slides alongside 
 *				  << 5 6 7 8 9 >> 
 * Usually the total number of items and the current page are carried along in the url or stored in the web session
 * so the the Paging is created as:				 
 * <pre class='brush:java'>
 *		Paging p = new Paging(10,5,		// 10 items / page, 5 pages / window
 *							  9,		// number of results
 *							  1);		// current page
 *		
 *		Iterator<PagingItem> it = p.getPagingIterator();
 *		while (it.hasNext()) {
 *			PagingItem pgItem = it.next();
 *			System.out.println(pgItem.debugInfo());
 *		} 
 * </pre>
 */
@Accessors(prefix="_")
public class Paging
  implements Serializable,
  			 Debuggable {
	
	private static final long serialVersionUID = -4966151337012633456L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////		
	@Getter private final int _pageItems;			// Items / page
	@Getter private final int _navBarWindowItems;	// Number of pages in the navigation bar: ie nav bar window = 5   << [1] [2] [3] [4] [5] >> 
	@Getter private final int _itemCount; 			// Total number of items found
	@Getter private final int _pageCount;			// number of pages
	
	@Getter private int _currentPage;  					// Current page
	@Getter private int[] _currentPageItems;			// Current page items 		(ie: 10,11,12,13,14,15 if _pageItems=15)
	@Getter private int[] _currentNavBarWindowPages;	// Current window pages		(ie: 1,2,3,4,5 if _navBarWindowItems=5)
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTORS
/////////////////////////////////////////////////////////////////////////////////////////
	/** 
	 * Default constructor
	 * @param totalItems the number of items returned by the search
	 * @param currPage the current page
	 */
	public Paging(final int totalItems,
				  final int currPage) throws IllegalArgumentException {
		this(10,	// default page size (items / page)
			 5,		// default nav bar window size
			 totalItems,
			 currPage);
	}
	/**
	 * Constructor setting the page size
	 * @param pageSize the number of items in each search results page
	 * @param totalItems the number of items returned by the search
	 * @param currPage the current page
	 */
	public Paging(final int pageSize,
				  final int totalItems,
				  final int currPage) throws IllegalArgumentException {
		this(pageSize,
			 5,					// default nav bar window size
			 totalItems,
			 currPage);
	}
	/**
	 * Constructor setting the page size and the nav bar window size
	 * @param pageSize the number of items in each search results page
	 * @param navBarWindowItems the number of pages shown in the nav bar (the window size)
	 * @param totalNumberOfItems the number of items returned by the search
	 * @param currPage the current page
	 */
	public Paging(final int pageSize,final int navBarWindowItems,
				  final int totalNumberOfItems,
				  final int currPage) throws IllegalArgumentException {
		Preconditions.checkArgument(pageSize > 0 || navBarWindowItems > 0,"The page size or the block of pages size cannot be zero or negative");
		Preconditions.checkArgument(totalNumberOfItems >= 0,"The total number of items must be a greater than zero");
		_pageItems = pageSize;					
		_navBarWindowItems = navBarWindowItems;
		
		_itemCount = totalNumberOfItems;
		_pageCount = _itemCount / _pageItems + (_itemCount % _pageItems != 0 ? 1 : 0);
		
		_currentPage = currPage;
		_computeState();
	} 
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Computes the state of the {@link Paging} instance:
	 * 	- The pages in the current window
	 * 	- The items in the current page
	 */
	private void _computeState() {
		// Get the current window pages: every window has the same number of pages, except the last one
		// that can have less pages
		int navBarWindows = this.getNavBarWindowsCount();			// the number of nav bar windows		
		int currentNavNarWindow = this.getCurrentNavBarWindow();	// the current nav bar window
		
		int currentWindowItems = currentNavNarWindow < navBarWindows || _pageCount % _navBarWindowItems == 0 ? _navBarWindowItems
																											 : _pageCount % _navBarWindowItems;
		// Return an array of the item numbers of the current window
		int currentNavBarWindowItem = currentNavNarWindow > 1 ? currentNavNarWindow * _navBarWindowItems - _navBarWindowItems + 1
															  : 1;
		int[] windowItems = new int[currentWindowItems];
		for (int i = 0; i < currentWindowItems; i++) {
			windowItems[i] = currentNavBarWindowItem++;
		}
		_currentNavBarWindowPages = windowItems;
		
		// Get the current page items
		int pageFirstItem = this.getPageFirstItem(_currentPage);
		int pageLastItem = this.getPageLastItem(_currentPage);
		int pagesInWindow = _pageCount > 0 ? (pageLastItem - pageFirstItem) + 1 : 0;
		_currentPageItems = _pageCount > 0 ? new int[pagesInWindow] : null;
		if (CollectionUtils.hasData(_currentPageItems)) {
			for (int i=0; i < pagesInWindow; i++) {
				_currentPageItems[i] = pageFirstItem + i;
			}		
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PAGES
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Returns the first page order number (allways 1)
	 * @return allways return 1
	 */
	@SuppressWarnings("static-method")
	public int getFirstPage() {
		return 1;
	}
	/**
	 * Returns the last page order number (the number of pages)
	 * @return the nubmer of pages
	 */
	public int getLastPage() {
		return _pageCount;
	}
	/**
	 * Returns the prev page number or -1 if the current page is the first page
	 * @return 
	 */
	public int getPrevPage() {
		if (_currentPage == 1) return -1;
		return _currentPage - 1;
	}
	/**
	 * Returns the next page number or -1 if the current page is the last page
	 * @return
	 */
	public int getNextPage() {
		if (_currentPage == _pageCount) return -1;
		return _currentPage + 1;
	}
	/**
	 * Returns true if there are more pages after the current one
	 * @return
	 */
	public boolean hasNextPage() {
		return _currentPage < _pageCount;
	}
	/**
	 * Returns true if there are pages before the current one
	 * @return
	 */
	public boolean hasPrevPage() {
		return _currentPage == 1 ? false
								 : _currentPage > 1;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PAGES
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the first item for the current page
	 * If there are for example 12 pages of 10 items each, and the current page 
	 * is the page 2, assuming 10 item/page, the current page (2) will
	 * have elements from 11 to 20. This method will return 11 
	 * @return the first page item
	 */
	public int getCurrentPageFirstItem() {
		return _currentPageItems[0];
	}
	/**
	 * Returns the last item for the current page
	 * If there are for example 12 pages of 10 items each, and the current page 
	 * is the page 2, assuming 10 item/page, the current page (2) will
	 * have elements from 11 to 20. This method will return 20 
	 * @return the last page item 
	 */
	public int getCurrentPageLastItem() {
		return _currentPageItems[_currentPageItems.length-1];
	}
	/**
	 * Returns the current page's previous page's last item number
	 * If there are for example 12 pages of 10 items each, and the current page 
	 * is the page 2, assuming 10 item/page, the current page's previous (1) will
	 * have elements from 1 to 10. This method will return 10 
	 * @return the current page's previous page's last item number
	 */
	public int getPreviousPageLastItem() {
		if (_currentPage == 1) throw new IllegalArgumentException("Illegal page");
		return this.getPageLastItem(_currentPage-1);
	}
	/**
	 * Returns the previous page's first item number
	 * @return
	 */
	public int getPreviousPageFirstItem() {
		return this.getPageFirstItem(this.getPrevPage());
	}
	/**
	 * Returns the current page's next page's first item number
	 * If there are for example 12 pages of 10 items each, and the current page 
	 * is the page 2, assuming 10 item/page, the current page's next (2) will
	 * have elements from 21 to 30. This method will return 21 
	 * @return the current page's next page's first item number
	 */   
	public int getNextPageFirstItem() {
		if (_currentPage == _pageCount) throw new IllegalArgumentException("Illegal page");
		return this.getPageFirstItem(_currentPage + 1);
	}
	/**
	 * Returns a page's first item
	 * @param page
	 * @return 
	 */
	public int getPageFirstItem(final int page) throws IllegalArgumentException {
		if (_pageCount == 0) return 0;
		if (page < 1 || page > _pageCount) throw new IllegalArgumentException("Illegal page");
		if (page == this.getFirstPage()) return 0;
		return (page * _pageItems) - _pageItems;
	}
	/**
	 * Returns a page's last item
	 * @param page 
	 * @return
	 */
	public int getPageLastItem(final int page) throws IllegalArgumentException {
		if (_pageCount == 0) return 0;
		if (page < 1 || page > _pageCount) throw new IllegalArgumentException("Illegal page");
		if (page == this.getLastPage() && _itemCount % _pageItems != 0) {
			int lastPageItems = _itemCount % _pageItems;
			return (page - 1) * _pageItems	// all pages items except the for last one
				 + lastPageItems;			// the last page items
		}
		return page * _pageItems;
	}
	/**
	 * Returns the last page's first item number
	 * @return
	 */
	public int getLastPageFirstItem() {
		return this.getPageFirstItem(this.getLastPage());
	}
	/**
	 * @return true if the pager is at the first page
	 */
	public boolean isFirstPage() {
		return _currentPage == 1;
	}
	/**
	 * @return  true if the pager is at the last page
	 */
	public boolean isLastPage() {
		return _currentPage == _pageCount;
	}
	/**
	 * @return true if there's another next page
	 */
	public boolean hasNextPages() {
		return _currentPage < _pageCount;
	}
	/**
	 * @return true if there's a previous page
	 */
	public boolean hasPreviousPage() {
		return _currentPage > 1;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  WINDOW FUNCTIONS
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns true if there is a window after the current one
	 * @return
	 */
	public boolean hasNextWindow() {
		return this.getCurrentNavBarWindow() < this.getNavBarWindowsCount();
	}
	/**
	 * Returns true if there is a window before the current one 
	 * @return
	 */
	public boolean hasPrevWindow() {
		return this.getCurrentNavBarWindow() == 1 ? false 
												  : this.getCurrentNavBarWindow() > 1;
	}
	/**
	 * Returns the number of windows in the navigation bar
	 * @return
	 */
	public int getNavBarWindowsCount() {
		int navBarWindows = _pageCount / _navBarWindowItems + (_pageCount % _navBarWindowItems != 0 ? 1 : 0);
		return navBarWindows;
	}
	/**
	 * Returns the actual window in the navigation bar
	 * @return
	 */
	public int getCurrentNavBarWindow() {
		int currentNavNarWindow = _currentPage / _navBarWindowItems + (_currentPage % _navBarWindowItems != 0 ? 1 : 0); 
		return currentNavNarWindow;
	}
	/**
	 * Returns the current window first page
	 * @return
	 */
	public int getCurrentNavBarWindowFirstPage() {
		return _currentNavBarWindowPages[0];
	}
	/**
	 * Returns the current window last page
	 * @return
	 */
	public int getCurrentNavBarWindowLastPage() {
		return _currentNavBarWindowPages[_currentNavBarWindowPages.length-1];
	}
	/**
	 * Returns the current window's previous window's last page
	 * @return
	 */
	public int getPrevNavBarWindowLastPage() {
		int prevBlockLastPage = _currentNavBarWindowPages[0] == 1 ? 1 : _currentNavBarWindowPages[0]-1;
		return prevBlockLastPage;
	}
	/**
	 * Returns the current window's next window's first page
	 * @return
	 */
	public int getNextNavBarWindowFirstPage() {
		int nextBlockFirstPage = _currentNavBarWindowPages[_currentNavBarWindowPages.length-1] == _pageCount ? _currentNavBarWindowPages[_currentNavBarWindowPages.length-1]
																											 : _currentNavBarWindowPages[_currentNavBarWindowPages.length-1]+1;
		return nextBlockFirstPage;
	}	
/////////////////////////////////////////////////////////////////////////////////////////
//  CURRENT PAGE MOVEMENT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Moves the current page to the page that contains the one
	 * with the given number
	 * @param itemNum
	 */
	public void goToPageWitItem(final int itemNum) {
		this.goToFirstPage();
		boolean found = false;
		do {
			for (int i=0; i < _currentPageItems.length; i++) {
				if (_currentPageItems[i] == itemNum) {
					found = true;
					break;
				}
			}
			if (!found && _currentPage < _pageCount) this.goToNextPage();
		} while (!found && _currentPage <= _pageCount);
		if (!found) throw new IllegalArgumentException("Could NOT find a page with item number=" + itemNum);
	}
	/**
	 * Moves the current page to the first page
	 */
	public void goToFirstPage() {
		_currentPage = 1;
		_computeState();
	}
	/**
	 * Moves the current page to the last page
	 */
	public void goToLastPage() {
		_currentPage = _pageCount;
		_computeState();
	} 
	/**
	 * Moves the current page to the previous page
	 * (if the current page is the first page, this method does nothing)
	 */
	public void goToPrevPage() {
		if (_currentPage == 1) throw new IllegalStateException("Illegal paging: cannot go to page=0"); 	
		_currentPage--;
		_computeState();
	}
	/**
	 * Moves the current page to the next page
	 * (if the current page is the last page, this method does nothing)
	 */
	public void goToNextPage() {
		if (_currentPage == _pageCount) throw new IllegalStateException("Illegal paging: cannot go to page=" + (_currentPage + 1) + " there are only " + _pageCount + " pages");	
		_currentPage++;
		_computeState();
	}
	/**
	 * Moves the current page to the given page
	 * @param newPage the page number to go
	 */
	public void goToPage(final int newPage) throws IllegalArgumentException {	   
		if (newPage <= 0 || newPage > _pageCount) throw new IllegalArgumentException("Illegal target page");
		_currentPage = newPage;
		_computeState();
	} 
	/**
	 * Moves the current page to the last page of the previous nav bar window
	 */
	public void goToPrevNavBarWindowLastPage() {
		_currentPage = this.getPrevNavBarWindowLastPage();
		_computeState();
	}
	/**
	 * Moves the current page to the first page of the next nav bar window
	 */
	public void goToNextNarBarWindowFirstPage() {
		_currentPage = this.getNextNavBarWindowFirstPage();
		_computeState();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  PAGE ITERATOR
/////////////////////////////////////////////////////////////////////////////////////////
	@Accessors(prefix="_")
	public class PagingItem
	  implements Debuggable {
		@Getter private final int _page;
		@Getter private final int _pageFirstItem;
		@Getter private final int _pageLastItem;
		
		@Getter private final int _window;
		@Getter private final int _windowFirstPage;
		@Getter private final int _windowLastPage;
		
		public PagingItem(final Paging p) {
			_page = p.getCurrentPage();
			_pageFirstItem = p.getCurrentPageFirstItem();
			_pageLastItem = p.getCurrentPageLastItem();
			
			_window = p.getCurrentNavBarWindow();
			_windowFirstPage = p.getCurrentNavBarWindowFirstPage();
			_windowLastPage = p.getCurrentNavBarWindowLastPage();
		}
		
		@Override
		public CharSequence debugInfo() {
			StringBuilder sb = new StringBuilder();
			
			sb.append(Strings.customized("Window {} ({}-{})",
										 _window,
										 _windowFirstPage,
										 _windowLastPage));
			int windowPageNum = _windowLastPage - _windowFirstPage; 
			sb.append(" [");
			for (int i=0; i <= windowPageNum; i++) {
				sb.append(_windowFirstPage + i);
				if (_windowFirstPage + i == _page) sb.append("*");
				if (i != windowPageNum) sb.append(" ");
			}
			sb.append("] ");
			
			sb.append(Strings.customized("curr page: <{}-{}>",
										 _pageFirstItem,
										 _pageLastItem));
			return sb.toString();
		}
	}
	/**
	 * Returns an iterator 
	 * @return
	 */
	public Iterator<PagingItem> getPagingIterator() {
		return new Iterator<PagingItem>() {
						private boolean _lastPage;
						
						@Override
						public boolean hasNext() {
							return !_lastPage;
						}
						@Override
						public PagingItem next() {
							PagingItem outItem = new PagingItem(Paging.this);
							if (Paging.this.hasNextPage()) {
								Paging.this.goToNextPage();
							} else {
								_lastPage = true;
							}
							return outItem;
						}
						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
			   };
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return new PagingItem(this).debugInfo(); 
	}
}
