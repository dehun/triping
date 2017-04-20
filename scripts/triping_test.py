import sys
import fileinput
import requests
import subprocess
import re

MY_IP = "95.85.4.47"
TRIPING_BASE = "http://178.62.51.207:9000/triping"

def test_ip(ip):
    # triping ping
    r = requests.get("%s/%s/%s" %(TRIPING_BASE, MY_IP, ip))
    triping_res = r.text
    # regular ping
    try:
        r = subprocess.check_output("ping -4 -c4 %s" % ip, shell=True)
        ping_res = re.search(r"rtt min/avg/max/mdev = .+?/(.+?)/.+", r).groups()[0]
    except Exception as ex:
        print  ex
        ping_res = "error"
    
    # print res
    print "%s | %s | %s" % (ip, triping_res, ping_res)

def ping_ips(ips):
    print "-------------------------------------------------------------------------------"
    print "       ip       |               triping                    |         ping      |"
    for ip in ips:
        test_ip(ip)

def main():
    ips = [ip.strip() for ip in fileinput.input()]
    ping_ips(ips)

if __name__ == '__main__':
    main()
