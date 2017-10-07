import { connect } from 'react-redux';
import * as actions from './actions';
import App from './app';

const mapStateToProps = state => ({
  connected: state.app.get('connected'),
  connectErrorMsg: state.app.get('connectErrorMsg'),
  databaseUrl: state.app.get('databaseUrl'),
  translateResult: state.app.get('translateResult'),
  queryResult: state.app.get('queryResult'),
});

const mapDispatchToProps = dispatch => ({
  connectUserSession: () => {
    dispatch(actions.connectUserSession());
  },
  disconnect: () => {
    dispatch(actions.disconnect());
  },
  connectToDB: (payload) => {
    dispatch(actions.connectToDB(payload));
  },
  connectToDemoDB: () => {
    dispatch(actions.connectToDemoDB());
  },
  translateNL: (payload) => {
    dispatch(actions.translateNL(payload));
  },
  executeSQL: (payload) => {
    dispatch(actions.executeSQL(payload));
  },
});

export default connect(mapStateToProps, mapDispatchToProps)(App);
