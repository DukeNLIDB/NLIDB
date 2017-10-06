import { fromJS } from 'immutable';
import * as actions from './actions';

const initialState = fromJS({
  connectSuccess: false,
  queryResult: null,
});

const reducers = (state = initialState, action) => {

  switch (action.type) {
    case actions.SET_CONNECT_INFO:
      return state.merge(action.payload);
    case actions.SET_QUERY_INFO:
      return state.merge(action.payload);
    default:
      return state;
  }
};

export default reducers;
