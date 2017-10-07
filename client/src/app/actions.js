
export const CONNECT_USER_SESSION = 'app/connect-user-session';
export const connectUserSession = () => ({
  type: CONNECT_USER_SESSION,
});

export const DISCONNECT = 'app/disconnect';
export const disconnect = () => ({
  type: DISCONNECT,
});

export const CONNECT_TO_DB = 'app/connect-to-db';
export const connectToDB = payload => ({
  type: CONNECT_TO_DB, payload,
});

export const CONNECT_TO_DEMODB = 'app/connect-to-demodb';
export const connectToDemoDB = () => ({
  type: CONNECT_TO_DEMODB,
});

export const TRANSLATE_NL = 'app/translateNL-nl';
export const translateNL = payload => ({
  type: TRANSLATE_NL, payload,
});

export const EXECUTE_SQL = 'app/execute-sql';
export const executeSQL = payload => ({
  type: EXECUTE_SQL, payload,
});

export const SET_APP_STATE = 'app/set-app-state';
export const setAppState = payload => ({
  type: SET_APP_STATE, payload,
});
