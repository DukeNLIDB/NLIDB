import React, { Component } from 'react';
import styled from 'styled-components';
import Form from '../common/form';
import SearchBar from './components/search-bar';
import buttonStyle from '../styles/button';

const Wrapper = styled.div`
  max-width: 1200px;
  margin: auto;
`;

const Title = styled.h1`
  text-align: center;
`;

const ResultText = styled.div`
  margin: 0 auto;
  width: 800px;
`;

const Text = styled.div`
  text-align: center;
  margin: 0 5px;
`;

const Button = styled.button`
  ${() => buttonStyle}
`;

const StatusBar = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const VerticalCenterDiv = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

class App extends Component {

  componentDidMount() {
    const { connectUserSession } = this.props;
    connectUserSession();
  }

  render() {
    const {
      connected, connectErrorMsg, databaseUrl, translateResult, queryResult,
      disconnect, connectToDB, connectToDemoDB, translateNL, executeSQL,
    } = this.props;

    return (
      <Wrapper>
        <Title>Natural Language Interface to DataBases</Title>
        {
          connected
            ? (
              <div>
                <StatusBar>
                  <Text>connected to {databaseUrl}</Text>
                  <Button onClick={disconnect}>disconnect</Button>
                </StatusBar>
                <SearchBar
                  title={'Natural Language Input:'}
                  submit={input => translateNL({ input })}
                  buttonTitle={'translate'}
                />
                <ResultText>{translateResult}</ResultText>
                <SearchBar
                  title={'SQL Query:'}
                  submit={input => executeSQL({ query: input })}
                  buttonTitle={'execute'}
                />
                <ResultText>
                  <pre>{queryResult}</pre>
                </ResultText>
              </div>
            )
            : (
              <VerticalCenterDiv>
                <Button onClick={connectToDemoDB}>connect to demo DB</Button>
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
                    name: 'connect',
                    submit: connectToDB,
                  }}
                />
              </VerticalCenterDiv>
            )
        }
      </Wrapper>
    );
  }
}

export default App;
