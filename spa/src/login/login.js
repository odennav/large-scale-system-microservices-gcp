import React, { Component } from 'react';
import axios from 'axios';

import LoginForm from './form.js';
import * as Constants from '../constant'

class Login extends Component {

    constructor(props) {
        super(props);
        this.authState = {isLoggedIn: false, userId: null, userName: null, accessToken: null};
        this.login = this.login.bind(this);
    }

    postLogin() {
        this.props.appAuthStateHandler(this.authState);
    }

    login(name, password) {
        const body = {id: name, password: password};
        console.log(JSON.stringify(body));
        axios.defaults.headers.common['Authorization'] = 'Basic d2ViLWNsaWVudDpzZWNyZXQ=';
        axios.post(Constants.AUTH_SVC_URL + "token", body)
        .then((response) => {
            console.log(response.data);
            console.log('Token request STATUS = '+response.status);
            return response.data.access_token;
        })
        .then((accessToken) => {
            console.log('Access token is '+accessToken);
            this.authState["accessToken"] = accessToken;
            return axios.get(Constants.AUTH_SVC_URL + "token/user?access_token="+accessToken, body)
        })
        .then((response) => {
            console.log('User request STATUS = '+response.status);
            this.result = 'User ' + response.data.name
                            + ' with roles: ' + JSON.stringify(response.data.roles);
            this.authState.isLoggedIn = true;
            this.authState.userId = body.id;
            this.authState.userName = response.data.name;
            this.postLogin();
        })
        .catch((error) => {
            console.log('Error description: '+error);
            if (error.response && error.response.status) {
                console.log('Error Status = '+error.response.status);
                if (error.response.status === 403) {
                    this.result = 'Incorrect credentials';
                }
            } else {
                this.result = 'Login failure';
            }
            this.authState["accessToken"] = null;
            this.postLogin(false);
        });
    }

    render() {
        const page = (this.authState.isLoggedIn) ?
                    <h1>You have logged in as</h1> :
                    <LoginForm loginHandler={this.login} />;     
        return (
              <div style={{marginTop:"5em"}}>
                    {page}                    
                    <h4>{this.result}</h4>
              </div>
            );
    }
}

export default Login;