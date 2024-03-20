import React, { Component } from 'react';

import './form.css';

class LoginForm extends Component {
    constructor(props) {
        super(props);
        this.state = {name: '', password: 'password'};
        this.handleChange = this.handleChange.bind(this);  
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({ [event.target.name] : event.target.value });
        event.preventDefault();
    }

    handleSubmit(event) {
        event.preventDefault();
        this.props.loginHandler(this.state.name, this.state.password);
    }

    render() {
        return (
              <div className="login col-md-offset-1 col-md-6">
                    <h3>Enter login credentials</h3>
                    <form className="form-group login-form" action="#" method="post" onSubmit={this.handleSubmit}>
                      <input className="form-control" type="text" name="name" value = {this.state.name} placeholder="user id" onChange={this.handleChange} />
                      <input className="form-control" type="password" name="password" value= {this.state.password} placeholder="password" onChange={this.handleChange} />
                      <input className="btn btn-primary" type="submit" value="Submit" />
                    </form>                
              </div>
            );
    }

}

export default LoginForm;