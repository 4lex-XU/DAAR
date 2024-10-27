// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.10;

abstract contract ERC721 {

    event Transfer(address indexed _from, address indexed _to, uint256 indexed _tokenId);
    event Approval(address indexed _owner, address indexed _approved, uint256 indexed _tokenId);

    function balanceOf(address _owner) public view virtual returns (uint256);
    function ownerOf(uint256 _tokenId) public view virtual returns (address);
    function transfer(address _to, uint256 _tokenId) public virtual;
}