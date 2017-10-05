
//Toggle between adding and removing the "responsive" class to
//topnav when the user clicks on the icon
function toggle() {
    var x = document.getElementById("myTopnav");
    if (x.className === "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
}


(function() {

    //home grown http lib
    var http = function () {
        this.get = function (url, callback) {
            var request = new XMLHttpRequest();
            request.onreadystatechange = function () {
                if (request.readyState == 4) {
                    if (request.status == 200) {
                        callback(request.responseText);
                    } else {
                        //TODO error handling for !200
                        window.location.replace('#!error');
                    }
                }
            };
            request.open('GET', url);
            //request.setRequestHeader("Authorization", "Basic " + btoa("username:password"));
            request.send();
        }
    }; var client = new http();

    function getId(id) {
        return document.getElementById(id);
    }

    function clarifyUserResource(url) {
        if (false) { //TODO check for login status of user
            return url;
        } else {
           return url.replace('articles', 'public');
        }
    }

    function generateHTML(url, id) {
        var resource = clarifyUserResource(url);
        client.get(resource, function (resp) {
            getId(id).innerHTML = resp;
        });
    }

    //load tags
    window.onload = function () {
        var template = getId('tags-template').innerHTML;
        Mustache.parse(template);
        var resource = clarifyUserResource('/api/articles/tagStubs');
        client.get(resource, function (resp) {
            getId('tags').innerHTML = Mustache.render(template, {tags: JSON.parse(resp)});
        });
    };

    //app routing
    var router = new Navigo(null, true, '#!');
    
    router.on({
        'frontpage' : () => {generateHTML('api/articles/frontpage', 'content')},
        'all'       : () => {generateHTML('api/articles/all', 'content')},
        'bookmarked': () => {generateHTML('api/articles/bookmarked', 'content')},
        'about'     : () => {generateHTML('pages/about.html', 'content')},
        'login'     : () => {generateHTML('pages/login.html', 'content')},
        'settings'  : () => {generateHTML('pages/settings.html', 'content')},
        'add'       : () => {generateHTML('pages/addFeeds.html', 'content')},
        'error'     : () => {generateHTML('pages/error.html', 'content')}
    });

    router.on('tagged', function (params, query) {
        generateHTML('api/articles/tagged?tag=' + query, 'content');
    }).resolve();

    //root view
    router.on(function () {
        generateHTML('api/articles/frontpage', 'content');
    });

    router.resolve();

})();


(function (window, document) {

    var layout   = document.getElementById('layout'),
        menu     = document.getElementById('menu'),
        menuLink = document.getElementById('menuLink'),
        content  = document.getElementById('main');

    function toggleClass(element, className) {
        var classes = element.className.split(/\s+/),
            length = classes.length,
            i = 0;

        for(; i < length; i++) {
            if (classes[i] === className) {
                classes.splice(i, 1);
                break;
            }
        }
        // The className is not found
        if (length === classes.length) {
            classes.push(className);
        }

        element.className = classes.join(' ');
    }

    function toggleAll(e) {
        var active = 'active';

        e.preventDefault();
        toggleClass(layout, active);
        toggleClass(menu, active);
        toggleClass(menuLink, active);
    }

    menuLink.onclick = function (e) {
        toggleAll(e);
    };

    content.onclick = function(e) {
        if (menu.className.indexOf('active') !== -1) {
            toggleAll(e);
        }
    };

}(this, this.document));


