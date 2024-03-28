using Knucklebones_server;
using System.Net;
using System.Net.Sockets;
using System.Text;

bool server_running = true;
try
{
    Messages messages = new Messages();
    IPHostEntry ipHostInfo = await Dns.GetHostEntryAsync("localhost");
    IPAddress ipAddress = ipHostInfo.AddressList[0];
    IPEndPoint ipEndPoint = new(ipAddress, 11_000);

    using Socket listener = new(
    SocketType.Stream,
    ProtocolType.Tcp);

    listener.Bind(ipEndPoint);
    listener.Listen(100);
    var handler = await listener.AcceptAsync();
    while (server_running)
    {
        var buffer = new byte[1_024];
        var received = await handler.ReceiveAsync(buffer, SocketFlags.None);
        var response = Encoding.UTF8.GetString(buffer,  0, received);

        if (response.IndexOf(messages.eom) > -1)
        {
            Console.WriteLine($"Server received: \"{response.Replace(messages.eom, "")}\"");

            var echoBytes = Encoding.UTF8.GetBytes(messages.ackMessage);
            await handler.SendAsync(echoBytes, 0);
            Console.WriteLine($"Server sent: \"{messages.ackMessage}\"");

            break;
        }
    }
}
catch (Exception e)
{
    Console.WriteLine($"ERROR {e.Message}");
}
