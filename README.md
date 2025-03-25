Milestone 2: Advanced Data Structures Implementation by John Paul P. DSA A2101


This repository contains the implementation of a comprehensive inventory management system for MotorPH, a motorcycle startup company. The system is built using a hybrid approach combining Binary Search Trees (BST) and Hash Tables to provide optimal performance for inventory operations.
Project Overview
MotorPH requires a system that allows them to:

Add new stock items to inventory
Delete incorrect stock entries (specifically those marked as "Old" or "Sold")
Sort inventory by motorcycle brand
Search for inventory items using various criteria

This implementation represents Milestone 2 of the development process, focusing on advanced data structures to improve performance and scalability as the inventory grows beyond its current 49 items.
Technical Implementation
The system utilizes a hybrid data structure approach:

Binary Search Tree (BST): Organizes inventory items in a hierarchical structure based on engine numbers, providing O(log n) operations for insertions, deletions, and searches when the tree is balanced.
Hash Tables: Multiple hash maps provide O(1) constant-time lookups for different attributes:

Engine number (primary key)
- Brand
- Status
- Purchase status


