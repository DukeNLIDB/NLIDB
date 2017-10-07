import { fromJS } from 'immutable';
import * as actions from './actions';

const initialState = fromJS({
  connected: false,
  connectErrorMsg: null,
  databaseUrl: null,
  translateResult: null,
  queryResult: null,
});

const reducers = (state = initialState, action) => {

  switch (action.type) {
    case actions.SET_APP_STATE:
      return state.merge(action.payload);
    default:
      return state;
  }
};

export default reducers;
