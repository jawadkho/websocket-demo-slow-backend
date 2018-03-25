import React, { Component } from 'react';
import './App.css';

const ReconnectingWebSocket = require('reconnecting-websocket');
const parseJson = require('parse-json');

class App extends Component {
  state = {endpoints: {}};
  componentDidMount() {
    var ws = new ReconnectingWebSocket('ws://localhost:4567/queues');

    ws.onclose = function() {
      console.log('echo-protocol Connection Closed');
    };
    var _this = this;
    ws.onmessage = function(message) {
      console.log('message received: ' + message.data);

      var o = parseJson(message.data);
      var k = o['endpoint']['name'];
      var v = o['attributes']['itemsAvailable'];

      var endpoints = {..._this.state.endpoints};
      endpoints[k] = v;
      _this.setState({
        endpoints: endpoints
      })
    }
  }
  render() {
    console.log(this.state)
    var rows = Object.keys(this.state.endpoints).sort().map((k, i) => {
      var v = this.state.endpoints[k];
      return (
        <tr>
          <th>{k}</th>
          <th>{v}</th>
        </tr>
      )
    })

    return (
      <div className="App">
        <table>
          <tr>
            <th>endpoint</th>
            <th>val</th>
          </tr>
          {rows}
        </table>
      </div>
    );
  }
}

export default App;
