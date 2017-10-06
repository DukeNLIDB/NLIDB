import { call, put, takeLatest } from 'redux-saga/effects';
import * as requests from '../requests';
import * as actions from './actions';

function* handleConnectUserSession() {
  const res = yield call(requests.connectUserSession());
  const body = yield res.json();
  if (body.success) {
    yield put(actions.setConnectInfo(body));
  }
}

function* handleConnectToDB(action) {
  const res = yield call(requests.connectToDB(action.payload));
  const body = yield res.json();
  yield put(actions.setConnectInfo(body));
}

function* handleQueryNL(action) {
  const res = yield call(requests.query(action.payload));
  const body = yield res.json();
  yield put(actions.setQueryInfo(body));
}

function* sagas() {
  yield takeLatest(actions.CONNECT_USER_SESSION, handleConnectUserSession);
  yield takeLatest(actions.CONNECT_TO_DB, handleConnectToDB);
  yield takeLatest(actions.QUERY_NL, handleQueryNL);
}

export default sagas;
