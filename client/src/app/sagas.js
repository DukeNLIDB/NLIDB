import { call, put, takeLatest } from 'redux-saga/effects';
import * as requests from '../requests';
import * as actions from './actions';

function* handleConnectUserSession() {
  const res = yield call(requests.connectUserSession);
  const { success, databaseUrl } = yield res.json();
  if (success) {
    yield put(actions.setAppState({
      connected: true,
      databaseUrl,
    }));
  }
}

function* handleDisconnect() {
  const res = yield call(requests.disconnect);
  if (res.status === 200) {
    yield put(actions.setAppState({
      connected: false,
      connectErrorMsg: null,
      databaseUrl: null,
      translateResult: null,
      queryResult: null,
    }));
  }
}

function* handleConnectToDB(action) {
  const res = yield call(requests.connectToDB, action.payload);
  const body = yield res.json();
  if (res.status === 200) {
    yield put(actions.setAppState({
      connected: true,
      connectErrorMsg: null,
      databaseUrl: body.databaseUrl,
    }));
  } else {
    yield put(actions.setAppState({
      connectErrorMsg: body.message,
    }));
  }
}

function* handleConnectToDemoDB() {
  const res = yield call(requests.connectToDB, {
    host: 'db-nlidb.cjna4ta2n7it.us-east-1.rds.amazonaws.com',
    port: 5432,
    database: 'demodb',
    username: 'keping',
    password: 'kepingwang'
  });
  const body = yield res.json();
  if (res.status === 200) {
    yield put(actions.setAppState({
      connected: true,
      connectErrorMsg: null,
      databaseUrl: body.databaseUrl,
    }));
  } else {
    yield put(actions.setAppState({
      connectErrorMsg: body.message,
    }));
  }
}

function* handleTranslateNL(action) {
  const res = yield call(requests.translateNL, action.payload);
  const body = yield res.json();
  yield put(actions.setAppState({
    translateResult: body.translateResult,
  }));
}

function* handleExecuteSQL(action) {
  const res = yield call(requests.executeSQL, action.payload);
  const body = yield res.json();
  yield put(actions.setAppState({
    queryResult: body.queryResult,
  }));
}

function* sagas() {
  yield takeLatest(actions.CONNECT_USER_SESSION, handleConnectUserSession);
  yield takeLatest(actions.DISCONNECT, handleDisconnect);
  yield takeLatest(actions.CONNECT_TO_DB, handleConnectToDB);
  yield takeLatest(actions.CONNECT_TO_DEMODB, handleConnectToDemoDB);
  yield takeLatest(actions.TRANSLATE_NL, handleTranslateNL);
  yield takeLatest(actions.EXECUTE_SQL, handleExecuteSQL);
}

export default sagas;
