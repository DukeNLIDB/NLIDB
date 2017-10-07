import React, { Component } from 'react';
import styled from 'styled-components';
import buttonStyle from '../../styles/button';

const Wrapper = styled.form`
  margin: 20px auto;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const LabelText = styled.div`
  display: inline-block;
  line-height: 31xpx;
`;

const Input = styled.input`
  height: 25px;
  width: 600px;
  margin: 0 5px;
  font-size: 16px;
  line-height: 25px;
`;

const SubmitButton = styled.input`
  ${() => buttonStyle}
`;

class SearchBar extends Component {
  constructor(props) {
    super(props);
    this.state = {
      input: '',
    };
  }

  handleChange(event) {
    this.setState({ input: event.target.value });
  }

  handleSubmit(event) {
    const { state: { input }, props: { submit } } = this;
    event.preventDefault();
    submit(input);
  }

  render() {
    const { title, buttonTitle } = this.props;

    return (
      <Wrapper onSubmit={e => this.handleSubmit(e)}>
        <LabelText>{title}</LabelText>
        <Input value={this.state.input} onChange={event => this.handleChange(event)} />
        <SubmitButton type="submit" value={buttonTitle} />
      </Wrapper>
    );
  }
}

export default SearchBar;
