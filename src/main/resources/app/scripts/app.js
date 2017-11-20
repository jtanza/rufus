//home grown http lib
var http = function () {

    this.get = function(url, success, error) {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function () {
            if (request.readyState == 4) {
                if (request.status == 200) {
                    success(request.responseText);
                } else {
                    error(request);
                }
            }
        };
        request.open('GET', url);
        var token = getStoredToken();
        if (token) {
            request.setRequestHeader('Authorization', 'Bearer ' + token);
        }
        request.send();
    };

    this.post = function(url, data, contentType, success, error) {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function () {
            if (request.readyState == 4) {
                if (request.status == 200) {
                    success(request.responseText);
                } else {
                    error(request);
                }
            }
        };
        request.open('POST', url);
        if (contentType) {
            request.setRequestHeader("Content-Type", contentType);
        }
        var token = getStoredToken();
        if (token) {
            request.setRequestHeader("Authorization", "Bearer " + token);
        }
        request.send(data);
    }
}; var client = new http();

function getId(id) {
    return document.getElementById(id);
}

function storeToken(resp) {
    sessionStorage.setItem('jwt_token', resp);
}

function getStoredToken() {
    return sessionStorage.getItem('jwt_token')
}

//generic error handling
function errorPage(errorResponse) {
    client.get('pages/error.html', function(resp) {
        Mustache.parse(resp);
        getId('content').innerHTML = Mustache.render(resp, {
            errCode: errorResponse.status
        });
    }, function(resp) {
        console.log("ruh-oh could not fetch error page, letting errors crop up to client");
        window.location.reload();
    });
}

//page rendering
(function() {
    function generateHTML(url, id) {
        client.get(url, function(resp) {
            getId(id).innerHTML = resp;
        }, function(resp) {
            errorPage(resp);
        });
    }

    window.addEventListener("load", function() {
        //load tags
        var template = getId('tags-template').innerHTML;
        Mustache.parse(template);
        client.get('api/articles/tagStubs', function (resp) {
            getId('tags').innerHTML = Mustache.render(template, {tags: JSON.parse(resp)});
        }, function(resp) {
            errorPage(resp);
        });

        //set login/logout based upon current session
        if (!getStoredToken()) {
            getId('bookmarked').setAttribute('class', 'inactive-link');
        } else {
            var login = getId('login');
            login.innerHTML = 'Logout';
            login.removeAttribute('href');
            login.onclick = function() {
                sessionStorage.removeItem('jwt_token');
                window.location.reload();
            }
        }
    });

    //app routing
    var router = new Navigo(null, true, '#!');

    //root view
    router.on(function () {
        generateHTML('api/articles/frontpage', 'content');
    });
    
    router.on({
        'frontpage' : () => {generateHTML('api/articles/frontpage', 'content')},
        'all'       : () => {generateHTML('api/articles/all', 'content')},
        'bookmarked': () => {generateHTML('api/articles/bookmarked', 'content')},
        'about'     : () => {generateHTML('pages/about.html', 'content')},
        'login'     : () => {generateHTML('pages/login.html', 'content')},
        'register'  : () => {generateHTML('pages/register.html', 'content')},
        'settings'  : () => {generateHTML('pages/settings.html', 'content')},
        'add'       : () => {generateHTML('pages/addFeeds.html', 'content')},
        'error'     : () => {generateHTML('pages/error.html', 'content')}
    });
    
    router.on('tagged', function(params, query) {
        generateHTML('api/articles/tagged?tag=' + query, 'content');
    }).resolve();
    
    router.resolve();
})();

//login
function login() {
    var formData = new FormData();
    formData.append("email", getId("emailInput").value);
    formData.append("password", getId("passwordInput").value);
    client.post('api/user/login', formData, null, function(resp) {
        storeToken(resp);
        window.location.assign('#!frontpage');
        window.location.reload();
    }, function(resp) {
        errorPage(resp);
    });
}

//register
function register() {
    var newUser = {};
    newUser.email = getId('emailInput').value;
    newUser.password = getId('passwordInput').value;

    var starterFeeds = [];
    var inputs = document.getElementsByTagName("input");
    for(var i = 0; i < inputs.length; i++) {
        if(inputs[i].type == "checkbox" && inputs[i].checked) {
            starterFeeds.push(inputs[i].id);
        }
    }
    newUser.starterFeeds = starterFeeds;

    client.post('api/user/new', JSON.stringify(newUser), 'application/json;charset=UTF-8', function(resp) {
        storeToken(resp);
        window.location.assign('#!frontpage');
        window.location.reload();
    }, function(resp) {
        errorPage(resp);
    });
}

//subscribe to new feeds
function subscribe() {
    var feeds = getId('subscriptionFeeds').value;
    var feedData = JSON.stringify(feeds.split(' '));
    client.post('api/articles/new', feedData, 'application/json;charset=UTF-8', function(resp) {
        alert(resp);
        window.location.assign('#!add');
        window.location.reload();
    }, function (resp) {
        errorPage(resp);
    });
}

//mock form enter behavior
function searchKeyPress(e) {
    e = e || window.event; //look for window.event in case event isn't passed in
    if (e.keyCode == 13) {
        getId('btnSearch').click();
        return false;
    }
    return true;
}


