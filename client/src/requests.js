const request = (url, payload) =>
  fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(payload),
  });

export const connectUserSession = () =>
  request('/api/connect/user/');

export const disconnect = () =>
  request('/api/disconnect');

export const connectToDB = ({ host, port, database, username, password }) =>
  request('/api/connect/db', { host, port, database, username, password });

export const translateNL = ({ input }) =>
  request('/api/translate/nl', { input });

export const executeSQL = ({ query }) =>
  request('/api/execute/sql', { query });
