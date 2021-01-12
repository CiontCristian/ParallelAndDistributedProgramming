using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Lab4.model
{
    public class Connection
    {
        public Socket socket = null;
        public string hostname;
        public string endpoint;
        public IPEndPoint remoteEndpoint;

        
        public ManualResetEvent connectDone = new ManualResetEvent(false);
        public ManualResetEvent sendDone = new ManualResetEvent(false);
    }
}