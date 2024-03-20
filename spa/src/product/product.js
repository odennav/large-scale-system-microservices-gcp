import React, { Component } from 'react';

import axios from 'axios';
import { Link } from 'react-router-dom';
import AppContext from '../app/app-context.js';
import * as Constants from '../constant'

class Product extends Component {
	constructor(props, context) {
		super(props);
		this.state = {remBtnVisible: "hidden"};
		this.getProduct(context.accessToken);
		this.qtyRef = React.createRef();
		this.userId = context.userId;
	}

	getCartId() {
		return this.userId;
	}

	getProduct(accessToken) {
		if (!accessToken) {
			console.log('No auth token provided');
			return;
		}
        axios.defaults.headers.common['Authorization'] = "Bearer "+accessToken;
        axios.get(Constants.PRODUCT_SVC_URL+this.props.productId)
        .then((response) => {
            console.log(response.data);
            console.log('Product request STATUS = '+response.status);
            var product = response.data;
        	var postfix_char = product.id.charAt(product.id.length-1)            
            product.imageUrl = Constants.PRODUCT_IMAGE_BASE_URL + postfix_char + ".jpeg"                    
            this.setState({product: response.data});
        })
        .then(() => {
	        axios.defaults.headers.common['Authorization'] = "Bearer "+accessToken;
	        axios.get(Constants.CART_SVC_URL+this.getCartId())
	        .then((response) => {
	            console.log(response.data);
	            console.log('Cart request STATUS = '+response.status);
	            let cart = response.data;
	            if (cart) {
	            	cart.cartLines.forEach((cartLine, index) => {
	            		if (cartLine.productId === this.props.productId) {
				            this.setState({remBtnVisible: 'visible'});
				            return;
	            		}
	            	});
	            }
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
                console.log('Unable to fetch product');
            }           
        })
	}

	addToCart(cartId, productId, event) {
		event.preventDefault();
	    const qtyNode = this.qtyRef.current;
	    const quantity = qtyNode.options[qtyNode.selectedIndex].value;
	    const params = new URLSearchParams();
	    params.append('id', cartId);
	    params.append('productId', productId);
	    params.append('quantity', quantity);
		const config = {
		  headers: {
		    'Content-Type': 'application/x-www-form-urlencoded',
		    'Authorization': 'Bearer '+this.context.accessToken
		  }
		}
        axios.post(Constants.CART_SVC_URL, params, config)
        .then((response) => {
            console.log(response.data);
            console.log('Product request STATUS = '+response.status);
            this.setState({remBtnVisible: "visible"});
        })
        .catch((error) => {
            console.log('Error description: '+error);
            if (error.response && error.response.status) {
                console.log('Error Status = '+error.response.status);
                if (error.response.status === 403) {
                    console.log('Invalid authorization token');
                }
            } else {
                console.log('Unable to add product to the cart');
            }           
        })
	}

	removeFromCart(cartId, productId, event) {
		event.preventDefault();
	    const params = new URLSearchParams();
	    params.append('id', cartId);
	    params.append('productId', productId);
	    params.append('quantity', 0);
		const config = {
		  headers: {
		    'Content-Type': 'application/x-www-form-urlencoded',
		    'Authorization': 'Bearer '+this.context.accessToken
		  }
		}
        axios.post(Constants.CART_SVC_URL, params, config)
        .then((response) => {
            console.log(response.data);
            console.log('Product request STATUS = '+response.status);
            this.setState({remBtnVisible: "hidden"});
        })
        .catch((error) => {
            console.log('Error description: '+error);
            if (error.response && error.response.status) {
                console.log('Error Status = '+error.response.status);
                if (error.response.status === 403) {
                    console.log('Invalid authorization token');
                }
            } else {
                console.log('Unable to remove product from the cart');
            }           
        })
	}

	render() {
		if (this.state.product) {
			const cartId = this.getCartId();
			const product = this.state.product;
			return (
					<div>

				        <div className="container">
				            <div className="row">
				                <div className="col-xs-12 col-md-5">
				                    <img style={{width:"100%"}} src={product.imageUrl} alt=""></img>
				                </div>
				                <div className="col-xs-12 col-md-7">
				                    <h2 style={{textTransform:"capitalize"}}>{product.name}</h2>
				                    <SampleText productName={product.name} />
				                    <div className="form-horizontal">
				                        <h3>{product.price} &#36;</h3>
				                        <div className="form-group">
				                            <div className="selectContainer" style={{padding:"1em"}}>
				                                <select  className="form-control" id="qtyAdd{{product.id}}" ref={this.qtyRef}>
				                                    <option value="1">1</option>
				                                    <option value="2">2</option>
				                                    <option value="3">3</option>
				                                    <option value="4">4</option>
				                                    <option value="5">5</option>
				                                </select>
				                            </div>
				                            <div>
				                                <button onClick={this.addToCart.bind(this, cartId, product.id)}
				                                   		className="btn btn-primary" style={{margin:"1em"}}>
				                                   		Add to cart
		                                   		</button>
				                                <button onClick={this.removeFromCart.bind(this, cartId, product.id)}
				                                   		className="btn btn-warning"
				                                   		style={{visibility: this.state.remBtnVisible, margin:"1em 0"}}>
				                                		Remove
			                                    </button>
				                                <Link to="/products" className="btn btn-info" style={{margin:"1em", float:"right"}} >Products</Link>
				                                <Link to="/cart" className="btn btn-success" style={{margin:"1em", float:"right"}} >Cart</Link>
				                            </div>
				                        </div>
				                    </div>
				                </div>
				            </div>
				        </div>


					</div>
				);
		}
		return <h1>Product</h1>;
	}
}

function SampleText(props) {
	return (<p>
				{props.productName}, internationally acclaimed textbook provides a comprehensive introduction to the
                modern study of computer algorithms. It covers a broad range of algorithms in depth, yet makes
                their design and analysis accessible to all levels of readers. Each chapter is relatively
                self-contained and presents an algorithm, a design technique, an application area, or a related
                topic. The algorithms are described and designed in a manner to be readable by anyone who has
                done a little programming. The explanations have been kept elementary without sacrificing depth
                of coverage or mathematical rigor. 
            </p>);
}

Product.contextType = AppContext;
export default Product;

