var reqA;
var reqD;
var removeButton;

var csrftoken = Cookies.get('csrftoken');

function callbackAddToCart() {
    if (reqA.readyState == 4) {
        if (reqA.status == 200) {
            removeButton.style.visibility = "visible";
        } else {
            alert("Could not add to cart!");
        }
    }
}

function handleAddToCartClick(productId, url) {
    var elt = document.getElementById('qtyAdd' + productId);
    var selection = elt.options[elt.selectedIndex].value;
    var params = 'productId=' + productId + '&quantity=' + selection
                        + '&action=add';
    reqA = new XMLHttpRequest();
    reqA.open("POST", url, true);
    reqA.setRequestHeader('X-CSRFToken',csrftoken);
    reqA.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    reqA.onreadystatechange = callbackAddToCart;
    reqA.send(params);
    removeButton = document.getElementById('removeButton' + productId);
}

function callbackRemoveFromCart() {
    if (reqD.readyState == 4) {
        if (reqD.status == 200) {
            removeButton.style.visibility = "hidden";
        } else {
            alert("Could not remove from cart!");
        }
    }
}

function handleRemoveFromCartClick(productId, url) {
    var params = 'productId=' + productId +'&action=remove';
    reqD = new XMLHttpRequest();
    reqD.open("POST", url, true);
    reqD.setRequestHeader('X-CSRFToken',csrftoken);
    reqD.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    reqD.onreadystatechange = callbackRemoveFromCart;
    reqD.send(params);
    removeButton = document.getElementById('removeButton' + productId);
}
