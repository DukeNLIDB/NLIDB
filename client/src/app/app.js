import React, { Component } from 'react';
import styled from 'styled-components';
import Form from '../common/form';
import SearchBar from './components/search-bar';

const Wrapper = styled.div`
  max-width: 1200px;
  margin: auto;
`;

const Title = styled.h1`
  text-align: center;
`;

const ResultText = styled.div`
  width: 800px;
`;


class App extends Component {

  componentDidMount() {
    const { connectUserSession } = this.props;
    connectUserSession();
  }

  render() {
    const {
      connectSuccess, connectToDB, connectErrorMsg,
      queryResult, queryNL,
    } = this.props;

    return (
      <Wrapper>
        <Title>Natural Language Interface to DataBases</Title>
        {
          connectSuccess
            ? (
              <div>
                <SearchBar submit={input => queryNL({ input })} />
                <ResultText>{queryResult}</ResultText>
              </div>
            )
            : (
              <Form
                key={1}
                message={connectErrorMsg}
                fields={[
                  { name: 'host' },
                  { name: 'port' },
                  { name: 'database' },
                  { name: 'username' },
                  { name: 'password', type: 'password' },
                ]}
                button={{
                  name: 'Connect',
                  submit: connectToDB,
                }}
              />
            )
        }
      </Wrapper>
    );
  }
}

export default App;
