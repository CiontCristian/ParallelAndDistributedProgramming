using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Lab4.model
{
    public class Response
    {
        public const int BUFFER_SIZE = 5120;
        public byte[] buffer = new byte[BUFFER_SIZE];
        
        public StringBuilder responseContent = new StringBuilder();

        public Socket socket = null;
        
        public ManualResetEvent receiveDone = new ManualResetEvent(false);
        
        public static string getRequestString(string hostname, string endpoint)
        {
            return "GET " + endpoint + " HTTP/1.1\r\n" +
                   "Host: " + hostname + "\r\n"+
                   "Content-Length: 0\r\n\r\n";
        }
    }
}