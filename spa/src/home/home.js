import React, { Component } from 'react';

import AppContext from '../app/app-context.js'

import './home.css';

class Home extends Component {

  	render() {

    	return (
      		<div>
        		<h1 className="text-center">Home</h1>
            <p style={{marginTop:"10em", textAlign:"center", wordWrap: "break-word"}}>{JSON.stringify(this.context)}</p>
      		</div>
    	);
  	}

  	componentDidMount() {
  		if (this.context.isLoggedIn && this.props.appAuthStateHandler) {
  		  this.props.appAuthStateHandler(null);
  		}  		
  	}
}

Home.contextType = AppContext;
export default Home;