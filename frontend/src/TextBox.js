import './App.css';

/**
* Functional component for text box elements on main page
*/
function TextBox(props) {

  // updates the variable associated with this TextBox
  // with the new value received
  const update = (event) => {props.change(event.target.value)}

  return (
    <div className="TextBox">
      <header className="TextBox" >

    <div className="TextBox">
    <label > {props.label}
      <input type={'text'} onChange = {update}>

     </input>
      </label>
    </div>
    </header>
    </div>
  );
}

export default TextBox;
