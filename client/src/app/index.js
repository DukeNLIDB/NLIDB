import { connect } from 'react-redux';
import * as actions from './actions';
import App from './app';

const mapStateToProps = state => ({
  connectSuccess: state.app.get('connectSuccess'),
  connectErrorMsg: state.app.get('connectErrorMsg'),
  queryResult: state.app.get('queryResult'),
});

const mapDispatchToProps = dispatch => ({
  connectUserSession: () => {
    dispatch(actions.connectUserSession());
  },
  connectToDB: (payload) => {
    dispatch(actions.connectToDB(payload));
  },
  queryNL: (payload) => {
    dispatch(actions.queryNL(payload));
  },
});

export default connect(mapStateToProps, mapDispatchToProps)(App);
