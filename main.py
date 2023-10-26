# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    import sys
    from sshpubkeys import SSHKey

    ssh = SSHKey(
        "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDNZpSKz163OPZo3nb/8kjHyrVWDl+4iE/FZ5toGMgkGIiss88IfkZtnIqjTe1iAL5frAsz6Y2CzY62zK+yYOZcg4DBNU5eH7aXkkcNRikVkvDfXjbWM9oDiJsjCG0sDbVr7rKXQ8l0T713L7oZTsP3JFIse7oeA4osrbDV4a23dNCzAkBINTdrRWY1I6LhA5nWinYkm2Wr0Dr3K2J0Q/HMLRRPe65GX95G0LsCXqgeCbSo2SNc0lebUsf13uFhrF5OaG8AtEJyjPVEeP2B3Dnb+vQXvVVfLWKcFpYuSyRDQ3evTrQbdCR8FuDJ2f4sfCJFsMxmFRQh+z8OLETB7DrPtrCb/TIebVeeDFo437RjThRUdNu8mbQ1X+SKX7L1XzskNNaOIu279Z1o9ngTyNvqZk1MKOaupqLsFes2yAWYLAt9EL2sd2XtGF6PO41JaFdHyzS24nbSqbj+4pQ7uXbQzwRt5dxZsJfbL5Ubg0X9heXevBUKH4DxaGcYQEIN6ia4WDJD6XeqrysYgyYIW6GJi0zJusMlbWY9qMRBsQ48b93D1f2ln2TkmEoDa9lWwRi/5OpBijg0yGAzDGasFC+Pw39Q0plXo4FgJuXekg+D/+klET2mVCAPt75PktI1rELafg9fa321Uht1GcC/o9G6PDfz3/PzT0WZgPR7CfUdbQ== yangximing123aa@163.com",
        strict_mode=True)

    try:
        ssh.parse()
    except Exception as err:
        print("Invalid key:", err)
        sys.exit(1)
    except NotImplementedError as err:
        print("Invalid key type:", err)
        sys.exit(1)

    print(ssh.bits)  # 768
    print(ssh.hash_md5())  # 56:84:1e:90:08:3b:60:c7:29:70:5f:5e:25:a6:3b:86
    print(ssh.hash_sha256())  # SHA256:xk3IEJIdIoR9MmSRXTP98rjDdZocmXJje/28ohMQEwM
    print(
        ssh.hash_sha512())  # SHA512:1C3lNBhjpDVQe39hnyy+xvlZYU3IPwzqK1rVneGavy6O3/ebjEQSFvmeWoyMTplIanmUK1hmr9nA8Skmj516HA
    print(ssh.comment)  # ojar@ojar-laptop
    print(ssh.options_raw)  # None (string of optional options at the beginning of public key)
    print(ssh.options)  # None (options as a dictionary, parsed and validated)
