import React, { Component } from 'react';

import { Link } from 'react-router-dom';
import { Nav, Navbar, NavDropdown } from 'react-bootstrap';
import { LinkContainer } from "react-router-bootstrap";
import AppContext from './app-context.js'

import './nav-panel.css'

class NavUser extends Component {
	render() {
        if (this.props.authState && this.props.authState.isLoggedIn === true) {
         	return (   	<NavDropdown title={this.props.authState.userName} id="basic-nav-dropdown">
	                		<NavDropdown.Item href="/logout">Sign Out</NavDropdown.Item>
	                		<NavDropdown.Divider />
			                <LinkContainer to="/test">
			                    <NavDropdown.Item href="/test">Test</NavDropdown.Item>
			                </LinkContainer>            
			            </NavDropdown>
		            );
        } else {
        	return (   	<NavDropdown title="Sign In" id="basic-nav-dropdown">
                			<NavDropdown.Item href="/login">Sign In</NavDropdown.Item>
            			</NavDropdown>
        			);
		}
	}
}

class NavPanel extends Component {
    render() {
    	return (
                    <div>
                        <Navbar bg="light" expand="lg">
                            <Navbar.Brand>
                                <Link className="nav-link-inherit" to="/home">
                                <img
                                    src="/images/logo.jpg"
                                    className="d-inline-block align-top"
                                    alt="React Bootstrap logo"
                                /></Link>    
                                </Navbar.Brand>
                            <Navbar.Toggle aria-controls="basic-navbar-nav" />
                            <Navbar.Collapse id="basic-navbar-nav">
                                <Nav className="mr-auto">
                                    <LinkContainer to="/home">
                                        <Nav.Link>Home</Nav.Link>
                                    </LinkContainer>
                                    <LinkContainer to="/products">
                                        <Nav.Link>Products</Nav.Link>
                                    </LinkContainer>
                                    <LinkContainer to="/cart">
                                        <Nav.Link>Cart</Nav.Link>
                                    </LinkContainer>
                                </Nav>
                                <Nav>
                                    <NavUser authState={this.context} />
                                </Nav>
                            </Navbar.Collapse>
                        </Navbar>
                    </div> 
    	);

	}

}

NavPanel.contextType = AppContext;
export default NavPanel;
