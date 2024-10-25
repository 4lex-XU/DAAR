// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.10;

/**
 * @title Ownable
 * @dev The Ownable contract has ann ower address, and provides basic authorization control
 * functions, this simplifies the implementation of "user permissions".
 */
contract Ownable {
    address public owner;

    event OwnershipTransferred(address indexed previousOwner, address indexed newOwner);

    /**
     * @dev The constructor sets the original `owner` of the contract to the sender
   * account.
   */
    constructor(address initialOwner) {
        owner = initialOwner;
    }


    /**
     * @dev Throws if called by any account other than the owner.
   */
    modifier onlyOwner() {
        require(msg.sender == owner, "L'appelant n'est pas l'administrateur");
        _;
    }


    /**
     * @dev Allows the current owner to transfer control of the contract to a newOwner.
   * @param newOwner The address to transfer ownership to.
   */
    function transferOwnership(address newOwner) public onlyOwner {
        require(newOwner != address(0));
        emit OwnershipTransferred(owner, newOwner);
        owner = newOwner;
    }

}
