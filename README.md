# CITS3002-Project

## Aim
- Build both wallet and miner software for a bitcoin-esque cryptocurrency.

## Requirements
### Wallet:
- Digital Wallet will be a file with a series of records which are successful transactions as recorded on the blockchain.
- Transaction records are of variable length sotre using JSON perhaps.
- Wallet needs to generate public and private keys using RSA with keysize of 2048 bits.
- Public and Private keys handled as PEM formatted text.
- All hashes and signatures will be SHA256.

### Miner:
- Recieves message from wallet, and will, add a nonce to the message.
- Will calc and RSA256 hash until it gets a hash that has first byte equal to 0.
- Nonce added to transaction and written to "blockchain", and all users can update their wallets (updating local blockchain) which alters their balance.

## Constraints
- Must write two distinct pieces of software (Miner and Wallet).
- Must execute across AT LEAST 3 different computers connected via a network.
- Can be developed for ONLY one Operating System.
- Project may be written in Python, Java, C99 or C++ (or a combination but not required).
- Traffic must be end-end encrypted via SSL.

## Process
1. Research into cryptocurrencies, SSL (in a language - probably Java or Python based on everyone's experiences) and start thinking about how software needs to work together.
2. Design how the wallet and miner will interact with one another.
3. Start working on which each of these programs (perhaps divide up the work, but not make it a rigid restriction).
4. Employ some good ol-fashioned Object Oriented design when writing software for each of the programs (if we split up into two groups, two members should work together to come up with function names and so on which shouldn't be changed)

## Ideas

Control Server:
- SSL Sockets
- Multi user
- Error handlings
    - Allows clients to report errors?
- GUI should be unnecessary

Wallet:
- SSL Sockets
- Allows creation of transactions
- Updates wallet
    - Asks other user for most up to date block chain
- GUI if we have time

Miner:
- SSL Socket
- Listens for transactions, adds to block, proof of work then sends updated block chain to other miners and to wallets.
- Revieves its own rewards
    - Show this via having another wallet open which is for miner and show they get rewards (not specificed but good addition)
- Miner handles its connection, reports errors, does operations on transactions
    **Needs to store block chain**
        - Use JSON
