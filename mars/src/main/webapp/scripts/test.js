(function() {
    /**
     * Variables
     */
    var user_id = '1111';
    var user_fullname = 'John Smith';
    var lng = -122.08;
    var lat = 37.38;

    console.log(user_id);
    console.log(user_fullname);
    function printGeo() {
        console.log(lng);
        console.log(lat);
    }
    printGeo();
    // initialize the page
    function init() {
        //login form event event listener
        document.querySelector("#login-form-btn").addEventListener('click', onSessionInvalid);
        //register form event listener
        document.querySelector("#register-form-btn").addEventListener('click', showRegisterForm);
        //register event listener
        document.querySelector("#register-btn").addEventListener('click', register);
        //login event listener
        document.querySelector("#login-btn").addEventListener('click', login);
        //loadNearbyItems
        document.querySelector('#nearby-btn').addEventListener('click',
            loadNearbyItems);
        //loadFavoriteItems
        document.querySelector('#fav-btn').addEventListener('click',
            loadFavoriteItems);
        //loadRecommendedItems
        document.querySelector('#recommend-btn').addEventListener('click',
            loadRecommendedItems);
        //logout event listener
        document.querySelector('#logout-link').addEventListener('click',
            onSessionInvalid);
        document.querySelector('#home').addEventListener('click',
            validateSession);
        document.querySelector('#contact').addEventListener('click',
            showContact);
        document.querySelector('#about').addEventListener('click',
            showAbout);
        validateSession();
    }
    // check if session if valid
    function validateSession() {
        onSessionInvalid();
        //set request
        let url = './login';
        let req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Validating session...');
        //send ajax request
        ajax('GET', url, req,
            function(res) {
                let result = JSON.parse(res);
                if (result.status === 'OK') {
                    console.log('Validate successfully!');
                    onSessionValid(result);
                } else {
                    console.log('Validate error!');
                }
            },
            function() {
                console.log('Validate error!');
            });
    }

    /* Register*/
    function register() {
        let username = document.querySelector('#register-username').value;
        let password = document.querySelector('#register-password').value;
        let lastName = document.querySelector('#register-last-name').value;
        let firstName = document.querySelector('#register-first-name').value;
        if (username === "" || password == "" || lastName === "" || firstName == "") {
            showRegisterResult("All fields with * are required!")
            return;
        }
        if (username.match(/^[a-z0-9_]+$/) === null) {
            showRegisterResult("Invalid username!")
            return;
        }
        password = md5(username + md5(password));
        //set http request
        let url = './register';
        let req = JSON.stringify({
            user_id : username,
            password : password,
            first_name : firstName,
            last_name : lastName,
        });

        //send request
        ajax('POST', url, req,
            //successful callback function
            function(res){
               let result = JSON.parse(res);
               if (result.status === 'OK') {
                   showRegisterResult('Registered successfully, please login.');
               } else {
                   showRegisterResult('Username already exists, please change username.');
               }
            },
            //error callback
            function(){
                showRegisterResult('Failed to register, please contact us.');
            });
    }

    /*login*/
    function login() {
        let username = document.querySelector('#username').value;
        let password = document.querySelector('#password').value;
        if (username === "" || password == "") {
            showLoginError("All fields with * are required!")
            return;
        }
        if (username.match(/^[a-z0-9_]+$/) === null) {
            showLoginError("Invalid username!")
            return;
        }
        password = md5(username + md5(password));
        //set http request
        let url = './login';
        let req = JSON.stringify({
            user_id : username,
            password : password,
        })
        ajax('POST', url, req,
            function(res) {
                let result = JSON.parse(res);
                if (result.status === 'OK') {
                    console.log('Login successfully!');
                    onSessionValid(result);
                } else {
                    showLoginError("Login failed!");
                }
            },
            function() {
                showLoginError("Login failed, invalid username and password!");
            });
    }

    /*ajax function to send request*/
    function ajax(method, url, data, successCallback, errorCallback) {
        let xhr = new XMLHttpRequest();
        xhr.open(method, url, true);

        //received the response
        xhr.onload = function() {
            if (xhr.status === 200) {
                successCallback(xhr.responseText);
            } else {
                console.error("Loaded, the request couldn't be completed.");
                errorCallback();
            }
        }
        xhr.onerror = function() {
            console.error("Error, the request couldn't be completed.");
            errorCallback();
        };

        if (data == null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type",
                "application/json;charset=utf-8");
            xhr.send(data);
        }
    }




    /*  Helper function to control the view*/
    //if session invalid, show only login form, hide the rest
    function onSessionInvalid() {
        let loginForm = document.querySelector('#login-form');
        let registerForm = document.querySelector('#register-form');
        let itemNav = document.querySelector("#item-nav");
        let itemList = document.querySelector("#item-list");
        let logoutBtn = document.querySelector("#logout-link");
        let avatar = document.querySelector("#avatar");
        let welcomeMsg = document.querySelector("#welcome-msg");
        hideElement(registerForm);
        hideElement(itemNav);
        hideElement(itemList);
        hideElement(logoutBtn);
        hideElement(welcomeMsg);
        hideElement(avatar);
        clearRegisterResult()
        showElement(loginForm);
        initGeoLocation();
    };
    //show register form when required
    function showRegisterForm() {
        let loginForm = document.querySelector('#login-form');
        let registerForm = document.querySelector('#register-form');
        let itemNav = document.querySelector("#item-nav");
        let itemList = document.querySelector("#item-list");
        let logoutBtn = document.querySelector("#logout-link");
        let avatar = document.querySelector("#avatar");
        let welcomeMsg = document.querySelector("#welcome-msg");
        hideElement(loginForm);
        hideElement(itemNav);
        hideElement(itemList);
        hideElement(logoutBtn);
        hideElement(welcomeMsg);
        hideElement(avatar);
        clearLoginError()
        showElement(registerForm);
    }
    //show valid session result
    function onSessionValid(result) {
        user_id = result.user_id;
        user_fullname = result.name;
        let loginForm = document.querySelector('#login-form');
        let registerForm = document.querySelector('#register-form');
        let itemNav = document.querySelector("#item-nav");
        let itemList = document.querySelector("#item-list");
        let logoutBtn = document.querySelector("#logout-link");
        let avatar = document.querySelector("#avatar");
        let welcomeMsg = document.querySelector("#welcome-msg");
        hideElement(loginForm);
        hideElement(registerForm);
        showElement(itemNav);
        showElement(itemList);
        showElement(logoutBtn, 'inline-block');
        showElement(welcomeMsg);
        showElement(avatar);
        initGeoLocation();
    }

    //helper function to show Element
    function showElement(element, style) {
        let displayStyle = style ? style : 'block';
        element.style.display = displayStyle;
    }
    //helper function to hide Element
    function hideElement(element) {
        element.style.display = 'none';
    }

    /*get GeoLocation */
    function initGeoLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(onPositionUpdated,
                onLoadPositionFailed, {
                    timeout: 30000,
                    maximumAge : 60000,
                });
            showLoadingMessage('Retrieving your location...');
        } else {
            onLoadPositionFailed();
        }
    }

    function onPositionUpdated(position) {
        lat = position.coords.latitude;
        lng = position.coords.longitude;
        console.log('lat -> ', lat);
        console.log('lng -> ', lng);
        loadNearbyItems();
    }

    function onLoadPositionFailed() {
        console.warn('navigator.geolocation is not available, trying to get location from ip.');
        getLocationFromIP();
    }

    function getLocationFromIP() {
        // get location from http://ipinfo.io/json
        let url = 'http://ipinfo.io/json'
        let data = null;

        ajax('GET', url, data, function(res) {
            let result = JSON.parse(res);
            if ('loc' in result) {
                let loc = result.loc.split(',');
                lat = loc[0];
                lng = loc[1];

            } else {
                console.warn('Getting location by IP failed, using default location.');
            }
            loadNearbyItems();

        });
    }
    /**
     * API #1 Load the nearby items API end point: [GET]
     * /search?user_id=1111&lat=37.38&lon=-122.08
     */
    function loadNearbyItems() {
        console.log('loadNearbyItems');
        activeBtn('nearby-btn');

        // The request parameters
        let url = './search';
        let params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
        let data = null;

        // display loading message
        showLoadingMessage('Loading nearby items...');

        // make AJAX call
        ajax('GET', url + '?' + params, data,
            // successful callback
            function(res) {
                let items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No nearby item.');
                } else {
                    console.log(items);
                    listItems(items);
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load nearby items.');
            });
    }
    /**
     * API #2 Load favorite (or visited) items API end point: [GET]
     * /history?user_id=1111
     */
    function loadFavoriteItems() {
        activeBtn('fav-btn');

        // request parameters
        let url = './history';
        let params = 'user_id=' + user_id;
        let req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading favorite items...');

        // make AJAX call
        ajax('GET', url + '?' + params, req, function(res) {
            let items = JSON.parse(res);
            if (!items || items.length === 0) {
                showWarningMessage('No favorite item.');
            } else {
                listItems(items);
            }
        }, function() {
            showErrorMessage('Cannot load favorite items.');
        });
    }

    /**
     * API #3 Load recommended items API end point: [GET]
     * /recommendation?user_id=1111
     */
    function loadRecommendedItems() {
        activeBtn('recommend-btn');

        // request parameters
        let url = './recommendation' + '?' + 'user_id=' + user_id + '&lat='
            + lat + '&lon=' + lng;
        let data = null;

        // display loading message
        showLoadingMessage('Loading recommended items...');

        // make AJAX call
        ajax('GET', url, data,
            // successful callback
            function(res) {
                let items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No recommended item. Make sure you have favorites.');
                } else {
                    listItems(items);
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load recommended items.');
            });
    }


    /**
     * API #4 Toggle favorite (or visited) items
     *
     * @param item -
     *            The item from the list
     *
     * API end point: [POST]/[DELETE] /history request json data: { user_id:
     * 1111, favorite: item }
     */
    function changeFavoriteItem(item) {
        // check whether this item has been visited or not
        let li = document.querySelector('#item-' + item.item_id);
        let favIcon = document.querySelector('#fav-icon-' + item.item_id);
        let favorite = !(li.dataset.favorite === 'true');

        // request parameters
        let url = './history';
        let req = JSON.stringify({
            user_id : user_id,
            favorite : item
        });
        let method = favorite ? 'POST' : 'DELETE';

        ajax(method, url, req,
            // successful callback
            function(res) {
                let result = JSON.parse(res);
                if (result.status === 'OK' || result.result === 'SUCCESS') {
                    li.dataset.favorite = favorite;
                    favIcon.className = favorite ? 'fa fa-heart' : 'fa fa-heart-o';
                }
            },
            //error
            function(){
                console.log('change favorite failed!')
            }
        );
    }

    /**
     * A helper function that creates a DOM element <tag options...>
     *
     * @param tag
     * @param options
     * @returns {Element}
     */
    function $create(tag, options) {
        let element = document.createElement(tag);
        for ( let key in options) {
            if (options.hasOwnProperty(key)) {
                element[key] = options[key];
            }
        }
        return element;
    }

    // -------------------------------------
    // Create item list
    // -------------------------------------
    /**
     * List recommendation items base on the data received
     *
     * @param items -
     *            An array of item JSON objects
     */
    function listItems(items) {
        let itemList = document.querySelector('#item-list');
        itemList.innerHTML = ''; // clear current results

        for (let i = 0; i < items.length; i++) {
            addItem(itemList, items[i]);
        }
    }

    /**
     * Add a single item to the list
     *
     * @param itemList -
     *            The
     *            <ul id="item-list">
     *            tag (DOM container)
     * @param item -
     *            The item data (JSON object)
     *
     * <li class="item"> <img alt="item image"
     * src="https://s3-media3.fl.yelpcdn.com/bphoto/EmBj4qlyQaGd9Q4oXEhEeQ/ms.jpg" />
     * <div> <a class="item-name" href="#" target="_blank">Item</a>
     * <p class="item-keyword">
     * Vegetarian
     * </p>
     * </div>
     * <p class="item-address">
     * 699 Calderon Ave<br/>Mountain View<br/> CA
     * </p>
     * <div class="fav-link"> <i class="fa fa-heart"></i> </div> </li>
     */
    function addItem(itemList, item) {
        let item_id = item.item_id;

        // create the <li> tag and specify the id and class attributes
        let li = $create('li', {
            id : 'item-' + item_id,
            className : 'item'
        });

        // set the data attribute ex. <li data-item_id="G5vYZ4kxGQVCR"
        // data-favorite="true">
        li.dataset.item_id = item_id;
        li.dataset.favorite = item.favorite;

        // item image
        if (item.image_url) {
            li.appendChild($create('img', {
                src : item.image_url
            }));
        } else {
            li.appendChild($create('img', {
                src : 'https://via.placeholder.com/100'
            }));
        }
        // section
        let section = $create('div');

        // title
        let title = $create('a', {
            className : 'item-name',
            href : item.url,
            target : '_blank'
        });
        title.innerHTML = item.name;
        section.appendChild(title);

        // keyword
        let keyword = $create('p', {
            className : 'item-keyword'
        });
        keyword.innerHTML = 'Keyword: ' + item.keywords.join(', ');
        section.appendChild(keyword);

        li.appendChild(section);

        // address
        let address = $create('p', {
            className : 'item-address'
        });

        // ',' => '<br/>', '\"' => ''
        address.innerHTML = item.address.replace(/,/g, '<br/>').replace(/\"/g,
            '');
        li.appendChild(address);

        // favorite link
        let favLink = $create('p', {
            className : 'fav-link'
        });

        favLink.onclick = function() {
            changeFavoriteItem(item);
        };

        favLink.appendChild($create('i', {
            id : 'fav-icon-' + item_id,
            className : item.favorite ? 'fa fa-heart' : 'fa fa-heart-o'
        }));

        li.appendChild(favLink);
        itemList.appendChild(li);
    }


    /**
     * A helper function that makes a navigation button active
     *
     * @param btnId -
     *            The id of the navigation button
     */
    function activeBtn(btnId) {
        let btns = document.querySelectorAll('.main-nav-btn');

        // deactivate all navigation buttons
        for (let i = 0; i < btns.length; i++) {
            btns[i].className = btns[i].className.replace(/\bactive\b/, '');
        }

        // active the one that has id = btnId
        let btn = document.querySelector('#' + btnId);
        btn.className += ' active';
    }

    function showLoadingMessage(msg) {
        let itemList = document.querySelector('#item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> '
            + msg + '</p>';
    }

    function showWarningMessage(msg) {
        let itemList = document.querySelector('#item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> '
            + msg + '</p>';
    }

    function showErrorMessage(msg) {
        let itemList = document.querySelector('#item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> '
            + msg + '</p>';
    }



    /*    helper function to handle msg*/
    //clear loginRegister msg
    function clearRegisterResult() {
        document.querySelector("#register-result").innerHTML='';
    }
    //clear login error msg
    function clearLoginError() {
        document.querySelector("#login-error").innerHTML='';
    }
    function showRegisterResult(registerMessage) {
        document.querySelector('#register-result').innerHTML = registerMessage;
    }
    function showLoginError(loginMessage) {
        document.querySelector('#login-error').innerHTML = loginMessage;
    }
    function showLoadingMessage(msg) {
        let itemList = document.querySelector('#item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> '
            + msg + '</p>';
    }
    function showContact() {
        alert("mingyan94@yahoo.com")
    }
    function showAbout() {
        alert("Job recommendation By Ming Yan, CO")
    }
    init();
})();
