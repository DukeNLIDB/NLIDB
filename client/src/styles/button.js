import { css } from 'styled-components';

const buttonStyle = css`
  background-color: #fff;
  border: none;
  cursor: pointer;
  padding: 6px 10px;
  text-align: center;
  display: inline-block;
  font-size: 14px;
  outline: none;
  box-shadow: 0px 0.5px 2px 1.5px #aaa;
  border: 1px solid #aaa;
  :active {
    box-shadow: none;
    border: 1px solid #777;
  }
  cursor: ${props => (props.disabled ? 'default' : 'pointer')};
  ${props => (props.disabled ? 'opacity: 0.65;' : '')}
`;

export default buttonStyle;
