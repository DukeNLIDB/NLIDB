
export const CONNECT_USER_SESSION = 'app/connect-user-session';
export const connectUserSession = () => ({
  type: CONNECT_USER_SESSION,
});

export const CONNECT_TO_DB = 'app/connect-to-db';
export const connectToDB = payload => ({
  type: CONNECT_TO_DB, payload,
});

export const SET_CONNECT_INFO = 'app/set-connect-info';
export const setConnectInfo = payload => ({
  type: SET_CONNECT_INFO, payload,
});

export const QUERY_NL = 'app/query-nl';
export const queryNL = payload => ({
  type: QUERY_NL, payload,
});

export const SET_QUERY_INFO = 'app/set-query-info';
export const setQueryInfo = payload => ({
  type: SET_QUERY_INFO, payload,
});
