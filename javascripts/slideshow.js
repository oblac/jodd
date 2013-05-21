$slideshow = {
    context: false,
    tabs: false,
    timeout: 5000,      // time before next slide appears (in ms)
    slideSpeed: 1000,   // time it takes to slide in each slide (in ms)
    tabSpeed: 300,      // time it takes to slide in each slide (in ms) when clicking through tabs
    fx: 'scrollLeft',   // the slide effect to use
    
    init: function() {
        // set the context to help speed up selectors/improve performance
        this.context = $('#slideshow');
        
        // set tabs to current hard coded navigation items
        this.tabs = $('ul.slides-nav li', this.context);
        
        // remove hard coded navigation items from DOM 
        // because they aren't hooked up to jQuery cycle
        this.tabs.remove();
        
        // prepare slideshow and jQuery cycle tabs
        this.prepareSlideshow();
    },
    
    prepareSlideshow: function() {
        // initialise the jquery cycle plugin -
        // for information on the options set below go to: 
        // http://malsup.com/jquery/cycle/options.html
        $('div.slides > ul', $slideshow.context).cycle({
            fx: $slideshow.fx,
            timeout: $slideshow.timeout,
            speed: $slideshow.slideSpeed,
            fastOnEvent: $slideshow.tabSpeed,
            pager: $('ul.slides-nav', $slideshow.context),
            pagerAnchorBuilder: $slideshow.prepareTabs,
            before: $slideshow.activateTab,
            pauseOnPagerHover: true,
            pause: true
        });            
    },
    
    prepareTabs: function(i, slide) {
        // return markup from hardcoded tabs for use as jQuery cycle tabs
        // (attaches necessary jQuery cycle events to tabs)
        return $slideshow.tabs.eq(i);
    },

    activateTab: function(currentSlide, nextSlide) {
        // get the active tab
        var activeTab = $('a[href="#' + nextSlide.id + '"]', $slideshow.context);
        
        // if there is an active tab
        if(activeTab.length) {
            // remove active styling from all other tabs
            $slideshow.tabs.removeClass('on');
            
            // add active styling to active button
            activeTab.parent().addClass('on');
        }            
    }            
};


$(function() {
    // initialise the slideshow when the DOM is ready
    $slideshow.init();
});  