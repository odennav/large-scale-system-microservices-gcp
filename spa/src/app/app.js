import React, { Component, Suspense, lazy } from 'react';

import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';

import NavPanel from './nav-panel.js'
import Home from '../home/home.js'
import AppContext from './app-context.js'
import ErrorBoundary from './error-boundary.js'

const renderLoader = () => <p>Loading</p>;

const Login = lazy(() => import('../login/login'));
const Products = lazy(() => import('../product/products'));
const Product = lazy(() => import('../product/product'));
const Cart = lazy(() => import('../order/cart'));

const AUTH_STATE_KEY = 'auth_state';
const AUTH_INIT_STATE = {isLoggedIn: false, userId: null, userName: null, accessToken: null};

class App extends Component {
    constructor(props) {
        super(props);
        this.state = AUTH_INIT_STATE;
        this.updateAppAuthState = this.updateAppAuthState.bind(this);
    }

    updateAppAuthState(authState) {
        authState = authState ? authState : AUTH_INIT_STATE; 
        sessionStorage.setItem(AUTH_STATE_KEY, JSON.stringify(authState));
        this.setState( {isLoggedIn: authState.isLoggedIn, userId: authState.userId, 
            userName: authState.userName, accessToken: authState.accessToken} );        
    }

    getAppAuthState() {
        return sessionStorage.getItem(AUTH_STATE_KEY) ? 
                JSON.parse(sessionStorage.getItem(AUTH_STATE_KEY)) : 
                {isLoggedIn: false, userId: null, userName: null, accessToken: null}; 
    }

    render() {
        const authState = this.getAppAuthState();
        return (
            <AppContext.Provider value={authState}>
                <div>
                    <Router>
                        <NavPanel />
                        <Suspense fallback={renderLoader()}>
                            <div className="container">
                                <ErrorBoundary>
                                    <Switch>
                                        <Route exact path="/"><Home /></Route>
                                        <Route path="/home"><Home /></Route>
                                        <Route exact path="/products"><Products /></Route>
                                        <Route path="/products/:id" render={ 
                                                        (props) => { return <Product productId={props.match.params.id} {... props} />} 
                                                    }  >
                                        </Route>
                                        <Route exact path="/cart"><Cart /></Route>
                                        <Route path="/login"><Login appAuthStateHandler={this.updateAppAuthState} /></Route>
                                        <Route path="/logout"><Home appAuthStateHandler={this.updateAppAuthState.bind(this)} /></Route>
                                        {/*<Route render={() => <h1>404: page not found</h1>} />*/}
                                    </Switch>
                                </ErrorBoundary>
                            </div>
                        </Suspense>
                    </Router>
                </div>
            </AppContext.Provider>
        );
    }

}

export default App;