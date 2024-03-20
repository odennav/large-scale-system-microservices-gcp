import React from 'react';

const AppContext = React.createContext({isLoggedIn: false, userId: null, userName: null, accessToken: null});
export default AppContext;