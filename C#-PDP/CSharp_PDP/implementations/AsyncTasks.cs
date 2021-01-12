using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Lab4.model;

namespace Lab4.implementations
{
    public class AsyncTasks
    {
        private  List<string> hosts;
        private const int port = 80;

        public AsyncTasks(List<String> hosts)
        {
            this.hosts = hosts;
        }

        public void run() {
            
            var tasks = new List<Task>();
            
            foreach (var host in hosts) {
                tasks.Add(Task.Factory.StartNew(BeginClient, host));
                //Thread.Sleep(5000);
            }

            Task.WaitAll(tasks.ToArray());
        }
        
        private static async void BeginClient(object hostObject)
        {
            var host = (String) hostObject;
              
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

              
            await Connect(connection);
            Response response = new Response();
            response.socket = client;
            await Send(connection, Response.getRequestString(connection.hostname, connection.endpoint));
            await Receive(response);

          
            Console.WriteLine("Response received:" + response.responseContent);
            
            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }

        private static async Task Connect(Connection connection) {
            connection.socket.BeginConnect(connection.remoteEndpoint, ConnectCallback, connection);

            await Task.FromResult(connection.connectDone.WaitOne());
        }

        private static void ConnectCallback(IAsyncResult ar) {
            var connection = (Connection) ar.AsyncState;
            
            var client = connection.socket;
            client.EndConnect(ar);

            Console.WriteLine("Socket successfully connected to  " + connection.hostname + " " + client.RemoteEndPoint);
            
            connection.connectDone.Set();
        }

        private static async Task Send(Connection connection, string data) { 
            var byteData = Encoding.ASCII.GetBytes(data);
            
            connection.socket.BeginSend(byteData, 0, byteData.Length, 0, SendCallback, connection);

            await Task.FromResult(connection.sendDone.WaitOne());
        }

        private static void SendCallback(IAsyncResult ar) {
            var connection = (Connection) ar.AsyncState;
            var client = connection.socket;
            
            var bytesSent  = client.EndSend(ar);
            Console.WriteLine("Sent " + bytesSent +" bytes to server.");
      
            connection.sendDone.Set();
        }

        private static async Task Receive(Response response) {
            response.socket.BeginReceive(response.buffer, 0, Response.BUFFER_SIZE, 0, ReceiveCallback, response);

            await Task.FromResult(response.receiveDone.WaitOne());
        }

        private static void ReceiveCallback(IAsyncResult ar)
        {
            var response = (Response) ar.AsyncState;
            var client = response.socket;
            
            var bytesRead = client.EndReceive(ar);
            
            response.responseContent.Append(Encoding.ASCII.GetString(response.buffer, 0, bytesRead));
            
            response.receiveDone.Set();
        }
        
     
    }
}