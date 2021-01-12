using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Lab4.implementations;

namespace Lab4
{
    class Program
    {
        private static readonly List<string> HOSTS = new List<string> {
            "www.youtube.com",
            "material.angular.io",
            "google.com", 
        };
        static void Main(string[] args)
        {
            Callback callback = new Callback(HOSTS);
            callback.run();
            Tasks tasks = new Tasks(HOSTS);
            //tasks.run();
            AsyncTasks asyncTasks = new AsyncTasks(HOSTS);
            //asyncTasks.run();
            
        }
    }
}