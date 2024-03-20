import React, { Component } from 'react';

import axios from 'axios';
import { Link } from 'react-router-dom';
import AppContext from '../app/app-context.js';
import * as Constants from '../constant'

import './products.css';


class Products extends Component {
	constructor(props, context) {
		super(props);
		this.state = {products: []};
		this.getProducts(context.accessToken);
	}

	getProducts(accessToken) {
		if (!accessToken) {
			console.log('No auth token provided');
			return;
		}

        axios.defaults.headers.common['Authorization'] = "Bearer "+accessToken;
        axios.get(Constants.PRODUCT_SVC_URL)
        .then((response) => {
            console.log(response.data);
            console.log('Product request STATUS = '+response.status);
            var products = response.data;
            for (var i=0; i<products.length; i++) {
            	var postfix_char = products[i].id.charAt(products[i].id.length-1)
            	products[i].imageUrl = Constants.PRODUCT_IMAGE_BASE_URL + postfix_char+".jpeg"
            	console.log(products[i]);
            }
            this.setState({products: response.data});
        })
        .catch((error) => {
            console.log('Error description: '+error);
            if (error.response && error.response.status) {
                console.log('Error Status = '+error.response.status);
                if (error.response.status === 403) {
                    console.log('Invalid authorization token');
                }
            } else {
                console.log('Unable to fetch products');
            }           
        })
	}

	render() {
		if (this.state.products) {
			const productsHtml = this.state.products.map((product) => 
	                <div className="col-sm-6 col-md-4" key={product.id}>
	                    <div className="thumbnail product-thumbnail">
	                        <Link to={ "/products/" + product.id }>
	                        	<img className="img-thumbnail img-fluid" src={product.imageUrl} alt="product name"/>
                        	</Link>
	                        <div className="product-details">
	                            <h3 style={{textTransform:"capitalize"}}>{product.name}</h3>
	                            <p id="name">{product.id}</p>
	                            <h4>{product.price} &#36;</h4>
	                        </div>
	                    </div>
	                </div>
				);

			return (
					<div>
					    <div className="container">
				            <div style={{textAlign:"center", padding:"2em 0"}}>
				                <h3>Products</h3>
				            </div>
		                    <div id="product-grid" className="row">
		             			{productsHtml}          					                        
		                    </div>
					    </div>
					</div>
				);
		}
		return <h1>Products</h1>;
	}
}

Products.contextType = AppContext;
export default Products;
