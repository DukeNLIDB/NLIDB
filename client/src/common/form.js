import React, { Component } from 'react';
import styled from 'styled-components';
import buttonStyle from '../styles/button';
import newId from '../utils/new-id';

const Wrapper = styled.form`
  margin: 20px;
  width: 230px;
`;

const FormItem = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
`;
const SubmitFormItem = FormItem.extend`
  justify-content: center;
  margin-top: 30px;
`;

const LabelText = styled.label`
  padding-right: 5px;
`;

const SubmitInput = styled.input`
  ${() => buttonStyle}
`;

const Message = styled.div`
  text-align: center;
`;

class Form extends Component {

  constructor(props) {
    super(props);
    const { fields } = this.props;
    this.state = fields.reduce((obj, field) => (
      Object.assign(obj, { [field.name]: field.initialValue || '' })
    ), {});
  }

  handleSubmit(event) {
    event.preventDefault();
    this.props.button.submit(this.state);
  }

  render() {
    const { fields, message, button } = this.props;
    const ids = fields.map(() => newId());
    return (
      <Wrapper onSubmit={e => this.handleSubmit(e)}>
        {message ? <Message>{message}</Message> : null}
        {fields.map((field, idx) => (
          <FormItem key={idx}>
            <label htmlFor={ids[idx]}>
              <LabelText>{field.displayName || field.name}:</LabelText>
              <input
                id={ids[idx]}
                type={field.type || 'text'}
                value={this.state[field.name]}
                onChange={(event) => {
                  this.setState({
                    [field.name]: event.target.value,
                  });
                }}
              />
            </label>
          </FormItem>
        ))}
        <SubmitFormItem>
          <SubmitInput type="submit" value={button.name} />
        </SubmitFormItem>
      </Wrapper>
    );
  }

}

export default Form;
