using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using Lab4.model;

namespace Lab4.implementations
{
    public class Callback
    {
        private List<string> hosts;
        private const int port = 80;

        public Callback(List<String> hosts)
        {
            this.hosts = hosts;
        }

        public void run()
        {
            foreach (var host in hosts)
            {
                BeginClient(host);
                //Thread.Sleep(5000);
            }
        }
        
        private void BeginClient(string host) {
            try {
                IPHostEntry ipHostEntry = Dns.GetHostEntry(host.Split('/')[0]);  
                IPAddress ipAddress = ipHostEntry.AddressList[0];  
                IPEndPoint remoteEndPoint = new IPEndPoint(ipAddress, port);
                
                var endpoint = host.Contains("/") ? host.Substring(host.IndexOf("/")) : "/";
                
                Socket client = new Socket(ipAddress.AddressFamily,SocketType.Stream, ProtocolType.Tcp);
                Connection connection = new Connection();
                connection.socket = client;
                connection.hostname = host.Split('/')[0];
                connection.endpoint = endpoint;
                connection.remoteEndpoint = remoteEndPoint;
    
                Connect(connection);
                connection.connectDone.WaitOne();
                
                Send(connection, Response.getRequestString(connection.hostname, connection.endpoint));
                connection.sendDone.WaitOne();
                
                Response response = new Response();
                response.socket = client;
                
                Receive(response);
                response.receiveDone.WaitOne();
                
                client.Shutdown(SocketShutdown.Both);  
                client.Close();

            } catch (Exception e) {  
                Console.WriteLine(e.ToString());  
            }  
        }
        
        private static void Connect(Connection connection) {
            connection.socket.BeginConnect(connection.remoteEndpoint, ConnectCallback, connection);
        }
        
        private static void ConnectCallback(IAsyncResult ar) {  
            try {
                var connection = (Connection) ar.AsyncState;
                var client = connection.socket;
                
                client.EndConnect(ar);
                Console.WriteLine("Socket successfully connected to " + client.RemoteEndPoint);
                connection.connectDone.Set();
                
            } catch (Exception e) {  
                Console.WriteLine(e.ToString());  
            }  
        }  
        
        private static void Send(Connection connection, String data) {
            byte[] byteData = Encoding.ASCII.GetBytes(data);
            connection.socket.BeginSend(byteData, 0, byteData.Length, 0, SendCallback, connection);  
        }  
  
        private static void SendCallback(IAsyncResult ar) {  
            try {
                Connection connection = (Connection) ar.AsyncState;
                var client = connection.socket;
                int bytesSent = client.EndSend(ar);  
                Console.WriteLine("Sent " + bytesSent +" bytes to server.");
                connection.sendDone.Set();
            
            } catch (Exception e) {  
                Console.WriteLine(e.ToString());  
            }  
        }
        
        private static void Receive(Response response) {  
            try
            {
                var client = response.socket;
                
                client.BeginReceive( response.buffer, 0, Response.BUFFER_SIZE, 0, ReceiveCallback, response);  
            } catch (Exception e) {  
                Console.WriteLine(e.ToString());  
            }  
        }  
  
        private static void ReceiveCallback( IAsyncResult ar ) {  
            try {
                Response response = (Response) ar.AsyncState;
                Socket client = response.socket;
                
                int bytesRead = client.EndReceive(ar);
                response.responseContent.Append(Encoding.ASCII.GetString(response.buffer,0,bytesRead));
                
                /*if (bytesRead > 0) {
                    client.BeginReceive(response.buffer,0,Response.BUFFER_SIZE,0,  
                        ReceiveCallback, response);  
                } else {
                    if (response.responseContent.Length > 1) {  
                        var result = response.responseContent.ToString();
                        Console.WriteLine("Response received: " + result);
                    }
                    response.receiveDone.Set();
                }  
                */
                
                
                var result = response.responseContent.ToString();
                Console.WriteLine("Response received: " + result);

                response.receiveDone.Set();
                
            } catch (Exception e) {  
                Console.WriteLine(e.ToString());  
            }  
        } 
       
    }
}