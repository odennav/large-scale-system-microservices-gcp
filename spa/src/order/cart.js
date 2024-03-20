import React, { useState, useContext, useEffect } from 'react';

import axios from 'axios';
import { Link } from 'react-router-dom';
import AppContext from '../app/app-context.js';
import * as Constants from '../constant'

import '../product/products.css';

function Cart(props) {

	const [cart, setCart] =useState(null);
	const context = useContext(AppContext);

	useEffect(() => {
		console.log('*************************');
		if (cart === null) getCart();
	})

	function getCartId() {
		return context.userId;
	}

	function getCart() {
		if (!context.accessToken) {
			console.log('No auth token provided');
			return;
		}

        axios.defaults.headers.common['Authorization'] = "Bearer "+context.accessToken;
        axios.get(Constants.CART_SVC_URL+getCartId())
        .then((response) => {
            console.log(response.data);
            console.log('Cart request STATUS = '+response.status);
            return response.data;
        })
        .then((newCart) => {
        	let requests = [];
        	newCart.cartLines.map((cartLine, index) => 
		        requests[index] = axios.get(Constants.PRODUCT_SVC_URL+cartLine.productId)
		    );
	        Promise.all(requests).then((responses) => {
		        	//responses.map((response, index) => newCart.cartLines[index].product = response.data);
		        	for (var i=0; i<responses.length; i++) {
		        		var product = responses[i].data;
		        		newCart.cartLines[i].product = product;
		            	var postfix_char = product.id.charAt(product.id.length-1)
		        		newCart.cartLines[i].product.imageUrl = Constants.PRODUCT_IMAGE_BASE_URL + postfix_char + ".jpeg";
		        	}
		        	console.log(newCart);
		        	setCart(newCart);
		        })
        })
        .catch((error) => {
            console.log('Error description: '+error);
            if (error.response && error.response.status) {
                console.log('Error Status = '+error.response.status);
                if (error.response.status === 403) {
                    console.log('Invalid authorization token');
                }
            } else {
                console.log('Unable to fetch the cart');
            }           
        })
	}

	const mycart = cart;

	if (mycart && mycart.cartLines) {
		const cartHtml = mycart.cartLines.map((cartLine) => 
                <div className="col-sm-6 col-md-4" key={cartLine.id}>
                    <div className="thumbnail product-thumbnail">
                        <Link to={ "/products/" + cartLine.productId }>
                        	<img className="img-thumbnail img-fluid" src={cartLine.product.imageUrl} alt="product name"/>
                    	</Link>
                        <div className="product-details">
                            <h4 style={{textTransform:"capitalize"}}>{cartLine.product.name}</h4>
                            <p>{cartLine.productId}</p>
                            <h5>Price: {cartLine.product.price} &#36;</h5>
                            <p>Qty: {cartLine.quantity}</p>
                        </div>
                    </div>
                </div>
			);

		return (
				<div>
				    <div className="container">
			            <div style={{textAlign:"center", padding:"2em 0"}}>
			                <h3>Cart</h3>
			            </div>
			            <div className="row">
				            <div className="col-12">
				                <div className="well">
				                    <div id="product-grid" className="row">
				             			{cartHtml}
				                    </div>
				                </div>
					        </div>
				        </div>
				    </div>
				</div>
			);
	}
	return <h1>Cart Empty</h1>;
	
}

export default Cart;
